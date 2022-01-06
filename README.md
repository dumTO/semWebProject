# semWebProject

Select on alarming :


SELECT ?objet2 ?objet5

WHERE {
https://territoire.emse.fr/kg/emse/fayol/ https://w3id.org/bot#hasStorey ?object
    {
    SELECT ?object ?objet2 ?objet3 ?objet5
        WHERE {
            ?object https://w3id.org/bot#hasSpace ?objet2
      {
        SELECT ?objet2 ?objet3 ?objet5
                WHERE {
                    ?objet2 ?predicate2 ?objet3
                      FILTER regex(str(?predicate2), 'datalink', 'i')
          {
            SELECT ?objet3 ?objet4 ?objet5
                WHERE {
                    ?objet3 ?predicate3 ?objet4.
                      ?objet3 ?predicate4 ?objet5
              FILTER (regex(str(?objet4), 'alarming', 'i') && regex(str(?predicate4), 'date', 'i'))
                }
            }
                }
            }
        }
    }
}




Select on diff :


SELECT DISTINCT ?objet2

WHERE {
<https://territoire.emse.fr/kg/emse/fayol/> <https://w3id.org/bot#hasStorey> ?object
	{
    SELECT ?object ?objet2 ?objet3
		WHERE {
			?object <https://w3id.org/bot#hasSpace> ?objet2
      {
    	SELECT ?objet2 ?objet3
				WHERE {
					?objet2 ?predicate2 ?objet3
          			FILTER regex(str(?predicate2), 'datalink', 'i')
          {
    		SELECT ?objet3 ?objet4
				WHERE {
					?objet3 ?predicate3 ?objet4
             		OPTIONAL { ?objet3 ?predicate4 ?objet5 }
              		FILTER regex(str(?predicate3), 'ext', 'i')
              		FILTER regex(str(?predicate4), 'temp', 'i')
              		FILTER ( abs(?objet5 - ?objet4) > 14)
				}
			}
				}
			}
		}
	}
}
