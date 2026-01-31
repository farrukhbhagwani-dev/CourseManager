Feature: Course View
  Specifications of the behavior of the Course View

  Scenario: The initial state of the view
    Given The database contains the courses with the following values
      | courseCode | courseTitle          | instructorName | creditHours | description        |
      | APT001     | Advanced Programming | Farrukh        | 6           | Simple course      |
      | APT002     | Database Systems     | Sara           | 6           | Database course    |
    When The Course View is shown
    Then The list contains elements with the following values
      | APT001 | Advanced Programming | Farrukh | 6 | Simple course   |
      | APT002 | Database Systems     | Sara    | 6 | Database course |

  Scenario: Add a new course
    Given The Course View is shown
    When The user enters the following values in the text fields
      | coursecode | coursetitle | instructorname | credithours | description     |
      | APT003     | Networks    | Iqbal            | 6           | Network course  |
    And The user clicks the "Add Course" button
    Then The list contains elements with the following values
      | APT003 | Networks | Iqbal | 6 | Network course |

  Scenario: Add a new course with an existing code
    Given The database contains the courses with the following values
      | courseCode | courseTitle          | instructorName | creditHours | description     |
      | APT001     | Advanced Programming | Farrukh        | 6           | Simple course   |
    And The Course View is shown
    When The user enters the following values in the text fields
      | coursecode | coursetitle | instructorname | credithours | description     |
      | APT001     | New Title   | New Instructor | 6           | Updated course  |
    And The user clicks the "Add Course" button
    Then An error is shown containing the following values
      | APT001 | Advanced Programming | Farrukh | 6 | Simple course |
