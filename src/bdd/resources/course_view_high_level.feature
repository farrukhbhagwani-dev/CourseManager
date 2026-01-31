Feature: Course View High Level
  Specifications of the behavior of the Course View

  Background:
    Given The database contains a few courses
    And The Course View is shown

  Scenario: Add a new course
    Given The user provides course data in the text fields
    When The user clicks the "Add Course" button
    Then The list contains the new course

  Scenario: Add a new course with an existing code
    Given The user provides course data in the text fields, specifying an existing code
    When The user clicks the "Add Course" button
    Then An error is shown containing the title of the existing course

  Scenario: Update an existing course
    Given The database contains the courses with the following values
      | courseCode | courseTitle           | instructorName | creditHours | description       |
      | APT001     | Advanced Programming  | Farrukh        | 6           | Simple course     |
    And The user selects the course with code "APT001" from the list
    When The user updates the course details with the following values
      | courseCode | courseTitle                  | instructorName | creditHours | description         |
      | APT001     | Advanced Programming Updated | Farrukh        | 6           | Updated description |
    Then The list reflects the updated details for the course with code "APT001"

  Scenario: Delete a course
    Given The user selects a course from the list
    When The user clicks the "Delete Selected" button
    Then The course is removed from the list

  Scenario: Delete a course that is not in the database anymore
    Given The user selects a course from the list
    But The course is in the meantime removed from the database
    When The user clicks the "Delete Selected" button
    Then An error is shown containing the title of the selected course
    And The course is removed from the list
