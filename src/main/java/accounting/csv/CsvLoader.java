package accounting.csv;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import accounting.data.CsvData;

public class CsvLoader {
   
   private final static Logger logger = LogManager.getLogger(CsvLoader.class);
   
   public Map<String, List<CsvData>> load(final String fileName) {
      CsvMapper csvMapper = new CsvMapper();
      CsvSchema schema = CsvSchema.emptySchema().withHeader(); 
       
      List<CsvData> readValues = new ArrayList<>();
      
      ObjectReader oReader = csvMapper.readerFor(CsvData.class).with(schema);
      try (Reader reader = new FileReader(fileName)) {
         MappingIterator<CsvData> mi = oReader.readValues(reader);
         while (mi.hasNext()) {
            CsvData current = mi.next();
            readValues.add(current);
         }
    
         Map<String, List<CsvData>> families = 
                readValues.stream()
                          .collect(Collectors.groupingBy(f -> f.getFamilyId()));
         
         var msg = String.format("Data of %d families loaded", families.size());
         System.out.println(msg);
      
         return families;
      } catch (Exception e) {
        logger.error("Error occurred while loading many to many relationship " +
                     "from file = " + fileName, e);
        return Collections.emptyMap();
      }

   }
}
