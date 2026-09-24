#!/usr/bin/env python3
"""Generate Kotlin data files for the CalcWorker native app from site sources.
Reads: ~/workspace/calcworker-native/specs/specs.json, CLEAN list,
       ~/workspace/calcworker-app/js/ai-widget.js (TOOLS_DB),
       ~/workspace/calcworker-app/js/currency-engine.js,
       ~/workspace/calcworker-app/tools/omnicalc.html (unit tables).
Writes Kotlin into app/src/main/java/com/zaviyanllc/calcworker/data/gen/.
"""
import json, os, re

HOME = os.path.expanduser('~')
NATIVE = os.path.join(HOME, 'workspace/calcworker-native')
SITE = os.path.join(HOME, 'workspace/calcworker-app')
OUT = os.path.join(HOME, 'workspace/calcworker-android/app/src/main/java/com/zaviyanllc/calcworker/data/gen')
os.makedirs(OUT, exist_ok=True)

specs = json.load(open(os.path.join(NATIVE, 'specs/specs.json')))
clean = set()
for l in open(os.path.join(NATIVE, 'CLEAN_CALCULATORS.md')):
    l = l.strip()
    if l.startswith('- ') and l.endswith('.kt'):
        clean.add(l[2:-3])

# ---------- TOOLS_DB (titles, keywords, formulas) ----------
src = open(os.path.join(SITE, 'js/ai-widget.js')).read()
m = re.search(r'const TOOLS_DB = (\[.*?\]);', src, re.S)
tools_db = json.loads(m.group(1))
db_by_slug = {}
for t in tools_db:
    slug = t['url'].split('/')[-1].replace('.html', '')
    db_by_slug[slug] = t

# ---------- label beautifier ----------
WORD_FIX = {
    'Amt': 'Amount', 'Pct': '%', 'Num': 'Number', 'Cc': 'Card', 'Mo': 'Monthly',
    'Ann': 'Annual', 'Prop': 'Property', 'Ins': 'Insurance', 'Bday': 'Birthday',
    'Biz': 'Business', 'Pandi': 'P&I', 'Piti': 'PITI', 'Pmi': 'PMI', 'Ltv': 'LTV',
    'Rmd': 'RMD', 'Irs': 'IRS', 'Dti': 'DTI', 'Apr': 'APR', 'Apy': 'APY',
    'Rpm': 'RPM', 'Cpm': 'CPM', 'Ltv': 'LTV', 'Cac': 'CAC', 'Roi': 'ROI',
    'Fha': 'FHA', 'Hdhp': 'HDHP', 'Ppo': 'PPO', 'Hsa': 'HSA', 'Fsa': 'FSA',
    'Ctc': 'CTC', 'Eitc': 'EITC', 'Rsu': 'RSU', 'Etf': 'ETF', 'Ira': 'IRA',
    'S corp': 'S-Corp', 'Llc': 'LLC', 'Dime': 'DIME', 'Tdee': 'TDEE',
}
def beautify(label, is_output=False):
    if not label:
        return label
    if is_output and label.lower().startswith('stat '):
        label = 'Total ' + label[5:]
    words = []
    for w in label.split():
        words.append(WORD_FIX.get(w, w))
    label = ' '.join(words)
    label = re.sub(r'\bRes\b', '', label).strip()
    label = re.sub(r'\s+', ' ', label)
    return label

UNIT_RE = re.compile(r'\(([^)]*)\)\s*$')
def split_unit(label):
    """Extract trailing parenthetical unit; return (clean_label, prefix, suffix)."""
    m = UNIT_RE.search(label or '')
    prefix, suffix = None, None
    if m:
        u = m.group(1).strip()
        label = label[:m.start()].strip()
        if u == '$':
            prefix = '$'
        elif u == '%':
            suffix = '%'
        elif u.startswith('$/'):
            prefix, suffix = '$', u[1:]
        else:
            suffix = u
    return label, prefix, suffix

KIND_MAP = {'number': 'NUMBER', 'range': 'SLIDER', 'select': 'SELECT',
            'checkbox': 'SWITCH', 'date': 'DATE', 'text': 'TEXT', 'textarea': 'TEXTAREA'}

# per-slug input label/unit overrides: slug -> {input_id: (label, prefix, suffix)}
INPUT_OVERRIDES = {
    'mortgage-calculator': {
        'homePrice': ('Home Price', '$', None),
        'downDollar': ('Down Payment', '$', None),
        'downPct': ('Down Payment', None, '%'),
        'interestRate': ('Interest Rate', None, '%'),
        'propTax': ('Property Tax / yr', '$', None),
        'insurance': ('Home Insurance / yr', '$', None),
    },
    'tip-calculator': {
        'billAmount': ('Bill Amount', '$', None),
        'tipPct': ('Tip', None, '%'),
        'numPeople': ('People', None, None),
    },
}
# outputs to drop (container/caption ids, not real results)
DROP_OUTPUTS = {
    'mortgage-calculator': {'resLoanLabel', 'resPMIRow'},
    'date-calculator': {'resAddSubBox', 'resBetweenBox'},
}
# extra whole-word output label fixes applied after WORD_FIX
OUTPUT_FIX = {'Pand I': 'Principal & Interest', 'Total Int': 'Total Interest',
              'Total Cost': 'Total of Payments', 'Per Bill': 'Bill per Person',
              'Per Tip': 'Tip per Person', 'Per Total': 'Total per Person',
              'Bill Amt': 'Bill Amount', 'Tip Amt': 'Tip Amount',
              'Total Bill': 'Total Bill', 'Age Primary': 'Your Age',
              'Age Detailed': 'Detailed Age', 'Next Bday': 'Next Birthday',
              'Born Weekday': 'Born On', 'Annual Rmd': 'Annual RMD',
              'Monthly Rmd': 'Monthly RMD', 'Tax Due': 'Est. Tax Due',
              'Irs Factor': 'IRS Factor', 'Rmd Percent': 'RMD %',
              'After Tax Rmd': 'After-Tax RMD', 'Penalty Notice': 'Penalty Note',
              'Target Date': 'Result Date', 'Between Days': 'Days Between',
              'Between Breakdown': 'Breakdown', 'Total Mo Pay': 'Monthly Payment',
              'Start Bal': 'Starting Balance', 'Total Paid': 'Total Paid',
              'Interest Saved': 'Interest Saved', 'Payoff Date': 'Payoff Date',
              'Months': 'Months to Payoff'}

def dnum(v):
    if v is None or v == '':
        return 'null'
    try:
        return repr(float(v))
    except Exception:
        return 'null'

SELECT_OPTIONS = {
    'paycheck-calculator': {
        'filingStatus': [('single', 'Single'), ('married', 'Married Filing Jointly'),
                         ('marriedSep', 'Married Filing Separately'), ('hoh', 'Head of Household')],
        'stateSelect': [('0', 'No State Tax (AK/FL/TX/WA...)'), ('0.05', 'Alabama (5.0%)'),
            ('0.025', 'Arizona (2.5%)'), ('0.047', 'Arkansas (4.7%)'), ('0.093', 'California (9.3%)'),
            ('0.044', 'Colorado (4.4%)'), ('0.05', 'Connecticut (5.0%)'), ('0.066', 'Delaware (6.6%)'),
            ('0.0925', 'Washington DC (9.25%)'), ('0.0549', 'Georgia (5.49%)'), ('0.0825', 'Hawaii (8.25%)'),
            ('0.058', 'Idaho (5.8%)'), ('0.0495', 'Illinois (4.95%)'), ('0.0315', 'Indiana (3.15%)'),
            ('0.0482', 'Iowa (4.82%)'), ('0.057', 'Kansas (5.7%)'), ('0.04', 'Kentucky (4.0%)'),
            ('0.0425', 'Louisiana (4.25%)'), ('0.0715', 'Maine (7.15%)'), ('0.0575', 'Maryland (5.75%)'),
            ('0.05', 'Massachusetts (5.0%)'), ('0.0425', 'Michigan (4.25%)'), ('0.0785', 'Minnesota (7.85%)'),
            ('0.05', 'Mississippi (5.0%)'), ('0.0495', 'Missouri (4.95%)'), ('0.0675', 'Montana (6.75%)'),
            ('0.0684', 'Nebraska (6.84%)'), ('0.0897', 'New Jersey (8.97%)'), ('0.059', 'New Mexico (5.9%)'),
            ('0.0685', 'New York (6.85%)'), ('0.045', 'North Carolina (4.5%)'), ('0.025', 'North Dakota (2.5%)'),
            ('0.0399', 'Ohio (3.99%)'), ('0.0475', 'Oklahoma (4.75%)'), ('0.099', 'Oregon (9.9%)'),
            ('0.0307', 'Pennsylvania (3.07%)'), ('0.0599', 'Rhode Island (5.99%)'),
            ('0.065', 'South Carolina (6.5%)'), ('0.0465', 'Utah (4.65%)'), ('0.0875', 'Vermont (8.75%)'),
            ('0.0575', 'Virginia (5.75%)'), ('0.0512', 'West Virginia (5.12%)'), ('0.0765', 'Wisconsin (7.65%)')],
    },
}

EXTRA_INPUTS = {
    'paycheck-calculator': [
        {'id': 'payPeriod', 'kind': 'select', 'label': 'Pay Type',
         'options': [{'value': 'hourly', 'label': 'Hourly'}, {'value': 'daily', 'label': 'Daily'},
                     {'value': 'weekly', 'label': 'Weekly'}, {'value': 'biweekly', 'label': 'Bi-Weekly'},
                     {'value': 'semimonthly', 'label': 'Semi-Monthly'}, {'value': 'monthly', 'label': 'Monthly'}],
         'value': 'hourly'},
    ],
    'mortgage-calculator': [
        {'id': 'loanTerm', 'kind': 'select', 'label': 'Loan Term',
         'options': [{'value': '10', 'label': '10 years'}, {'value': '15', 'label': '15 years'},
                     {'value': '20', 'label': '20 years'}, {'value': '30', 'label': '30 years'}],
         'value': '30'},
    ],
}
# link down-payment $/% pair for mortgage
LINK_PAIRS = {('mortgage-calculator', 'downDollar', 'downPct'): 'percent'}

def kstr(s):
    if s is None:
        return 'null'
    return '"' + s.replace('\\', '\\\\').replace('"', '\\"').replace('\n', '\\n') + '"'

def gen_inputs(spec):
    out = []
    inputs = list(spec['inputs']) + EXTRA_INPUTS.get(spec['slug'], [])
    # detect range->number pairing
    body_ids = set()
    for e in spec.get('functions', []):
        body_ids |= set(re.findall(r'inp\.(?:num|str|int|bool)\("([^"]+)"', e.get('body', '')))
    num_ids = [i['id'] for i in inputs if i['kind'] == 'number']
    for i in inputs:
        kind = KIND_MAP.get(i['kind'], 'TEXT')
        ov = INPUT_OVERRIDES.get(spec['slug'], {}).get(i['id'])
        if ov:
            label, prefix, suffix = ov
        else:
            label, prefix, suffix = split_unit(beautify(i.get('label') or i['id'], False))
        bind_to = None
        if i['kind'] == 'range' and i['id'] not in body_ids:
            stem = re.sub(r'(slider|range)$', '', i['id'], flags=re.I).lower()
            for nid in num_ids:
                if nid.lower().startswith(stem) and len(stem) >= 3:
                    bind_to = nid
                    break
        opts = [dict(value=v, label=lb) for v, lb in SELECT_OPTIONS.get(spec['slug'], {}).get(i['id'], [])] or i.get('options') or []
        opt_str = ', '.join('CalcOption(%s, %s)' % (kstr(o['value']), kstr(o.get('label') or o['value'])) for o in opts)
        out.append('CalcInput(%s, %s, InputKind.%s, %s, %s, %s, %s, listOf(%s), %s, %s, %s)' % (
            kstr(i['id']), kstr(label), kind,
            dnum(i.get('min')), dnum(i.get('max')), dnum(i.get('step')),
            kstr(i.get('value') or ''),
            opt_str, kstr(bind_to), kstr(prefix), kstr(suffix)))
    return out

def gen_outputs(spec):
    drops = DROP_OUTPUTS.get(spec['slug'], set())
    out = []
    for o in spec.get('outputs', []):
        if o['id'] in drops:
            continue
        label = OUTPUT_FIX.get(beautify(o.get('label') or o['id'], True),
                               beautify(o.get('label') or o['id'], True))
        out.append('CalcOutput(%s, %s)' % (kstr(o['id']), kstr(label)))
    return out

POPULAR = {'mortgage-calculator', 'compound-interest', 'tip-calculator', 'bmi-calculator',
           'currency-converter', 'auto-loan', 'paycheck-calculator', 'age-calculator',
           'break-even', 'crypto-profit-calculator', 'fuel-cost-calculator', 'sales-tax-calculator'}

HAND = ['mortgage-calculator', 'compound-interest', 'paycheck-calculator', 'age-calculator',
        'date-calculator', 'credit-card-payoff', '401k-rmd-calculator']
AICOST = ['ai-token-calculator', 'ai-prompt-cost-calculator', 'openai-api-cost-calculator',
          'claude-api-cost-calculator']
CURRENCY = {'currency-converter': ('USD', 'EUR'), 'usd-to-eur': ('USD', 'EUR'),
            'usd-to-gbp': ('USD', 'GBP'), 'usd-to-cad': ('USD', 'CAD'),
            'usd-to-mxn': ('USD', 'MXN'), 'usd-to-jpy': ('USD', 'JPY'),
            'usd-to-inr': ('USD', 'INR'), 'usd-to-pkr': ('USD', 'PKR')}

included = []  # (spec, kind)
for e in specs:
    slug = e['slug']
    if slug in clean or slug in HAND or slug in AICOST:
        included.append((e, 'FORMULA'))
    elif slug in CURRENCY:
        included.append((e, 'CURRENCY'))
    elif slug == 'omnicalc':
        included.append((e, 'OMNICALC'))

lines = []
lines.append('package com.zaviyanllc.calcworker.data.gen')
lines.append('')
lines.append('import com.zaviyanllc.calcworker.data.*')
lines.append('')
lines.append('/** Auto-generated from site sources — do not hand-edit. */')
lines.append('object SpecData {')
lines.append('  val specs: List<CalcSpec> = listOf(')
for e, kind in included:
    slug = e['slug']
    db = db_by_slug.get(slug, {})
    title = db.get('title', e['title']).replace(' (2026)', '').replace(' 2026', '')
    keywords = db.get('keywords', [])
    formula = (db.get('formula') or '')[:400]
    howto = re.sub(r'^\d+\.\s*', '', db.get('how_to_use') or '')
    blurb = (howto.split('. ')[0] + '.')[:160] if howto else ''
    protip = (db.get('pro_tip') or '')[:220]
    cat = e.get('category') or 'Finance & Loans'
    if slug == 'omnicalc':
        cat = 'Science & Math'
    ins = gen_inputs(e)
    outs = gen_outputs(e)
    cur_from, cur_to = CURRENCY.get(slug, (None, None))
    lines.append('    CalcSpec(')
    lines.append('      slug = %s,' % kstr(slug))
    lines.append('      title = %s,' % kstr(title))
    lines.append('      category = %s,' % kstr(cat))
    lines.append('      keywords = listOf(%s),' % ', '.join(kstr(k) for k in keywords[:12]))
    lines.append('      blurb = %s,' % kstr(blurb))
    lines.append('      formula = %s,' % kstr(formula))
    lines.append('      proTip = %s,' % kstr(protip))
    lines.append("      inputs = listOf(\n        " + (",\n        ").join(ins) + "\n      ),")
    lines.append('      outputs = listOf(%s),' % ', '.join(outs))
    lines.append('      kind = CalcKind.%s,' % kind)
    lines.append('      popular = %s,' % ('true' if slug in POPULAR else 'false'))
    lines.append('      currencyFrom = %s, currencyTo = %s,' % (kstr(cur_from), kstr(cur_to)))
    lines.append('    ),')
lines.append('  )')
lines.append('}')
open(os.path.join(OUT, 'SpecData.kt'), 'w').write('\n'.join(lines))
print('SpecData:', len(included), 'specs')

# ---------- AiCatalog ----------
lines = ['package com.zaviyanllc.calcworker.data.gen', '',
         '/** Compact AI catalog: title, slug, keywords, formula. Auto-generated. */',
         'object AiCatalogData {', '  val entries: List<AiEntry> = listOf(']
for e in specs:
    slug = e['slug']
    db = db_by_slug.get(slug, {})
    title = db.get('title', e['title'])
    kw = db.get('keywords', [])[:10]
    formula = (db.get('formula') or '')[:280]
    lines.append('    AiEntry(%s, %s, listOf(%s), %s),' % (
        kstr(title), kstr(slug), ', '.join(kstr(k) for k in kw), kstr(formula)))
lines.append('  )')
lines.append('}')
open(os.path.join(OUT, 'AiCatalogData.kt'), 'w').write('\n'.join(lines))
print('AiCatalog:', len(specs), 'entries')

# ---------- FxData ----------
fx = open(os.path.join(SITE, 'js/currency-engine.js')).read()
rates = dict(re.findall(r'(\w+):\s*([\d.]+)', fx.split('var CURRENCIES')[0].split('BASELINE_RATES = {')[1]))
meta = re.findall(r"(\w+):\s*\{\s*symbol:\s*'([^']*)',\s*name:\s*'([^']*)'\s*\}", fx)
lines = ['package com.zaviyanllc.calcworker.data.gen', '',
         '/** Sep-2026 baseline FX rates vs USD + currency metadata. Auto-generated. */',
         'object FxData {',
         '  val baseline: Map<String, Double> = mapOf(']
for code, rate in rates.items():
    lines.append('    %s to %s,' % (kstr(code), rate))
lines.append('  )')
lines.append('  val meta: Map<String, Pair<String, String>> = mapOf(')
for code, sym, name in meta:
    lines.append('    %s to (%s to %s),' % (kstr(code), kstr(sym), kstr(name)))
lines.append('  )')
lines.append('}')
open(os.path.join(OUT, 'FxData.kt'), 'w').write('\n'.join(lines))
print('FxData:', len(rates), 'rates,', len(meta), 'meta')

# ---------- OmniUnits ----------
omni = open(os.path.join(SITE, 'tools/omnicalc.html')).read()
block = omni.split('this.categories = {', 1)[1]
# crude brace-matched extraction per category
lines = ['package com.zaviyanllc.calcworker.data.gen', '',
         '/** Unit conversion tables ported from OmniCalc Ultra. Auto-generated. */',
         'data class OmniUnit(val code: String, val name: String, val factor: Double)',
         'data class OmniCategory(val key: String, val name: String, val specialTemp: Boolean, val units: List<OmniUnit>)',
         'object OmniUnitsData {',
         '  val categories: List<OmniCategory> = listOf(']
for m in re.finditer(r"(\w+):\s*\{\s*name:\s*'([^']+)'.*?units:\s*\{(.*?)\}\s*\}", block, re.S):
    key, name, units_block = m.group(1), m.group(2), m.group(3)
    special = 'isSpecial' in m.group(0) and 'temperature' in key
    units = []
    for um in re.finditer(r"(\w+):\s*\{\s*name:\s*'([^']+)'(?:,\s*(?:factor|rate):\s*([\d.* ]+))?", units_block):
        code, uname, factor = um.group(1), um.group(2), um.group(3)
        f = 1.0
        if factor:
            try:
                f = eval(factor.strip(), {'__builtins__': {}})
            except Exception:
                f = 1.0
        units.append((code, uname, f))
    lines.append('    OmniCategory(%s, %s, %s, listOf(' % (kstr(key), kstr(name), 'true' if special else 'false'))
    for code, uname, f in units:
        lines.append('      OmniUnit(%s, %s, %s),' % (kstr(code), kstr(uname), repr(float(f))))
    lines.append('    )),')
lines.append('  )')
lines.append('}')
open(os.path.join(OUT, 'OmniUnitsData.kt'), 'w').write('\n'.join(lines))
print('OmniUnits written')

# ---------- CalcDispatcher ----------
HAND = ['mortgage-calculator', 'compound-interest', 'paycheck-calculator', 'age-calculator',
        'date-calculator', 'credit-card-payoff', '401k-rmd-calculator']
AICOST = ['ai-token-calculator', 'ai-prompt-cost-calculator', 'openai-api-cost-calculator',
          'claude-api-cost-calculator']
dl = ['package com.zaviyanllc.calcworker.data', '',
      'import com.zaviyanllc.calcworker.calc.*', '',
      '/** Routes a calculator slug to its compiled formula function. Auto-generated. */',
      'object CalcDispatcher {',
      '  fun run(slug: String, inp: Inp): LinkedHashMap<String, String> = when (slug) {']
for e, kind in included:
    slug = e['slug']
    if kind != 'FORMULA':
        continue
    fn = 'calc_' + slug.replace('-', '_')
    dl.append('    %s -> %s(inp)' % (kstr(slug), fn))
dl.append('    else -> linkedMapOf("error" to "Calculator not available")')
dl.append('  }')
dl.append('}')
open(os.path.join(HOME, 'workspace/calcworker-android/app/src/main/java/com/zaviyanllc/calcworker/data/CalcDispatcher.kt'), 'w').write('\n'.join(dl))
print('CalcDispatcher:', sum(1 for _, k in included if k == 'FORMULA'), 'functions')
