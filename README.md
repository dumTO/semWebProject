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


SELECT ?objet2

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
					?objet3 ?predicate3 ?objet4.
             		?objet3 ?predicate4 ?objet5.
              FILTER (regex(str(?predicate3), 'temp', 'i') && regex(str(?predicate4), 'date', 'i'))
              		{
                SELECT ?objet5bis ?objet6bis ?objet7bis ?objet8bis ?objet9bis
				WHERE {
                  	<https://territoire.emse.fr/kg/emse/fayol/TemperatureExt> ?predicate5bis ?objet6bis
                  {
                    SELECT ?objet7bis ?objet8bis ?objet9bis
                    WHERE{
                    ?objet6bis ?predicate6bis ?objet7bis
                      FILTER regex(str(?predicate6bis), 'data', 'i')
                      {
                        SELECT ?objet8bis ?objet9bis
                        WHERE{
                          ?objet7bis ?predicate3bis ?objet8bis.
                      		?objet7bis ?predicate4bis ?objet9bis
              FILTER (regex(str(?predicate4bis), 'temp', 'i') && regex(str(?predicate3bis), 'date', 'i'))
                        }
                      }
                      
                      
                      
                  }
					
                    	
                  
                }
                
              		}
              }
              FILTER ( abs(?objet9bis - ?objet4) > 14  && YEAR(?objet8bis) = YEAR(?objet5) && MONTH(?objet8bis) = MONTH(?objet5) && HOURS(?objet8bis) = HOURS(?objet5))
				}
			}
				}
			}
		}
	}
}