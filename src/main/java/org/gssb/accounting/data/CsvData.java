package org.gssb.accounting.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Jacksonized
@ToString
@EqualsAndHashCode
@Getter
public class CsvData {

   @JsonProperty("FamilyCode")
   String familyCode;
   @JsonProperty("FamilyID")
   String familyId;
   @JsonProperty("ParentHelperChoice1")
   String parentHelperChoice;
   @JsonProperty("ApplicationGradeCode")
   int grade;

}
