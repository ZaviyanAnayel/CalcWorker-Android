package com.zaviyanllc.calcworker.calc

import kotlin.math.*

fun calc_dog_cat_age_calculator(inp: Inp): LinkedHashMap<String, String> {
    val out = LinkedHashMap<String, String>()

  try {
    
            val species = inp.str("petSpecies");
            val size = inp.str("dogBreedSize");
            val years =J.orD((inp.num("petCalendarYears")), (0));
            val months =J.orD((inp.num("petCalendarMonths")), (0));
            val totalYears = years + (months / 12);

            var humanAge = 0.0;
            var lifeStage = "Adult";
            var lifeExp = "12 - 15 Years";
            var agingRate = "4 yrs / yr";
            var seniorAge = "7 Years";
            var vetSchedule = "Annual wellness exam";
            var screening = "Routine vaccines, dental check & heartworm";

            if (species == "cat") {
                lifeExp = "14 - 18 Years";
                seniorAge = "10 Years";
                if (totalYears <= 1) {
                    humanAge = totalYears * 15;
                    lifeStage = "Junior / Kitten";
                } else if (totalYears <= 2) {
                    humanAge = 15 + ((totalYears - 1) * 9);
                    lifeStage = "Young Adult";
                } else {
                    humanAge = 24 + ((totalYears - 2) * 4);
                    agingRate = "4 human yrs / yr";
                    lifeStage =(if (totalYears >= 11) ((if (totalYears >= 15) "Geriatric" else "Senior")) else "Mature Adult");
                }
            } else {
                
                var ratePerYear = 4.5;
                if (size == "small") {
                    ratePerYear = 4.0;
                    lifeExp = "14 - 17 Years";
                    seniorAge = "10 - 11 Years";
                } else if (size == "medium") {
                    ratePerYear = 4.8;
                    lifeExp = "12 - 14 Years";
                    seniorAge = "8 - 9 Years";
                } else if (size == "large") {
                    ratePerYear = 5.5;
                    lifeExp = "10 - 12 Years";
                    seniorAge = "6 - 7 Years";
                } else if (size == "giant") {
                    ratePerYear = 7.5;
                    lifeExp = "7 - 9 Years";
                    seniorAge = "5 Years";
                }

                if (totalYears <= 1) {
                    humanAge = totalYears * 15;
                    lifeStage = "Puppy";
                } else if (totalYears <= 2) {
                    humanAge = 15 + ((totalYears - 1) * 9);
                    lifeStage = "Young Adult";
                } else {
                    humanAge = 24 + ((totalYears - 2) * ratePerYear);
                    agingRate = ratePerYear + " human yrs / yr";
                    if (humanAge >= 70) lifeStage = "Geriatric";
                    else if (humanAge >= 50) lifeStage = "Senior";
                    else lifeStage = "Adult";
                }
            }

            if (J.orD((lifeStage == "Senior"), (lifeStage == "Geriatric"))) {
                vetSchedule = "Semi-annual (every 6 months) comprehensive exam";
                screening = "Full blood chemistry, urinalysis, thyroid, blood pressure & arthritis mobility";
            }

            out["resHumanAgeEquivalent"] = J.s(J.round(humanAge) + " Years Old");
            out["resLifeStage"] = J.s(lifeStage);
            out["resAvgLifeExpectancy"] = J.s(lifeExp);

            out["resAgingRate"] = J.s(agingRate);
            out["resSeniorMilestone"] = J.s(seniorAge);
            out["resVetSchedule"] = J.s(vetSchedule);
            out["resScreeningFocus"] = J.s(screening);out["resSummary"] = J.s("Pet Biological Age: Your ${species} (${J.toFixed((totalYears), (1).toInt())} calendar years) is biologically equivalent to a ${J.round(humanAge)}-year-old human (${lifeStage} stage). Expected lifespan: ${lifeExp}. Recommended checkup: ${vetSchedule}."); return out;
        
  } catch (e: Exception) {
    
  }

    return out
}
