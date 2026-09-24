package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_instagram_money_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

    try {
      val followers =J.orD((inp.num("followerCount")), (0));
      val er =J.orD((inp.num("engagementRate")), (3.8));
      val nicheMult =J.orD((inp.num("nicheSelect")), (1.25));
      val usageMult =J.orD((inp.num("usageRights")), (1.0));
      val deals =J.orD((inp.num("monthlyDeals")), (2));
      val taxRate =J.orD((inp.num("taxRate")), (25));

      val erDesc =(if (er < 2.0) "Low" else ((if (er <= 4.5) "Good" else "High Viral")));
      out["erValDisplay"] = J.s(J.toFixed((er), (1).toInt()) + "% (" + erDesc + ")");

      out["taxRateDisplay"] = J.s(taxRate + "%");

      val erMultiplier = J.max(0.6, J.min(2.5, er / 3.2));
      val baseReel = J.max(50, (followers * 0.0105) * erMultiplier * nicheMult * usageMult);
      val carouselRate = baseReel * 0.88;
      val staticPhoto = baseReel * 0.70;
      val storyRate = baseReel * 0.40;
      val storyBundle3 = storyRate * 2.3;
      val fullPackage = (baseReel + carouselRate + storyRate) * 0.85;

      val grossMonthly = (baseReel * deals);
      val taxReserveAmount = grossMonthly * (taxRate / 100);
      val netMonthly = grossMonthly - taxReserveAmount;

      val estViews = J.round(followers * (er / 100) * 4.2);
      out["estViewsDisplay"] = J.s("Est. Median Views: ~" +J.locDf((estViews)));

      out["kpiReelRate"] = J.s("${'$'}" +J.locDf((J.round(baseReel))));
      out["kpiStoryRate"] = J.s("${'$'}" +J.locDf((J.round(storyRate))));
      out["kpiCarouselRate"] = J.s("${'$'}" +J.locDf((J.round(carouselRate))));
      out["kpiBundleRate"] = J.s("${'$'}" +J.locDf((J.round(fullPackage))));
      out["kpiMonthlyGross"] = J.s("${'$'}" +J.locDf((J.round(grossMonthly))));

      out["rowReel"] = J.s("${'$'}" +J.locDf((J.round(baseReel))));
      out["rowCarousel"] = J.s("${'$'}" +J.locDf((J.round(carouselRate))));
      out["rowPhoto"] = J.s("${'$'}" +J.locDf((J.round(staticPhoto))));
      out["rowStory"] = J.s("${'$'}" +J.locDf((J.round(storyRate))));
      out["rowStory3"] = J.s("${'$'}" +J.locDf((J.round(storyBundle3))));
      out["rowBundle"] = J.s("${'$'}" +J.locDf((J.round(fullPackage))));
      out["rowTaxReserve"] = J.s("-${'$'}" +J.locDf((J.round(taxReserveAmount))));
      out["rowNetMonthly"] = J.s("${'$'}" +J.locDf((J.round(netMonthly))));
    } catch (err: Exception) {
      
    }
  
    return out
}
