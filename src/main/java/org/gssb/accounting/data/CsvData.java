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

//   @JsonProperty("Student Code")
//   String studentCode;
//   @JsonProperty("Student ID")
//   String studentID;
//   @JsonProperty("Student First Name")
//   String studentFirstName;
//   @JsonProperty("Student Last Name")
//   String studentLastName;
//   @JsonProperty("Family Tags")
//   String familyTags;
//   @JsonProperty("Family Name")
//   String familyName;
   
   @JsonProperty("Family Code")
   String familyCode;
   @JsonProperty("Family ID")
   String familyId;
   @JsonProperty("Parent Helper Choice 1")
   String parentHelperChoice;
   @JsonProperty("Application Grade Code")
   int grade;
   
}
