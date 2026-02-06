package com.example.coursemanager.view.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.example.coursemanager.controller.CourseController;
import com.example.coursemanager.model.Course;
import com.example.coursemanager.view.CourseView;

public class CourseSwingView extends JFrame implements CourseView {

    private static final long serialVersionUID = 1L;

    private static final int DESCRIPTION_MAX_LENGTH = 200;

    private JTextField txtCourseCode;
    private JTextField txtCourseTitle;
    private JTextField txtInstructorName;
    private JTextField txtCreditHours;
    private JTextField txtDescription;

    private JButton btnAdd;
    private JButton btnCancel;
    private JButton btnUpdateSelected;
    private JButton btnDeleteSelected;

    private JLabel lblErrorMessage;

    private JList<Course> listCourses;
    private DefaultListModel<Course> listCourseModel;

    private transient CourseController courseController;

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }

    public DefaultListModel<Course> getListCourseModel() {
        return listCourseModel;
    }

    public CourseSwingView() {
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Course Manager");

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 45, 0, 770, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 173, 0, 23, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        getContentPane().setLayout(gridBagLayout);

        JLabel lblCourseCode = new JLabel("Course Code");
        GridBagConstraints gbc_lblCourseCode = new GridBagConstraints();
        gbc_lblCourseCode.anchor = GridBagConstraints.EAST;
        gbc_lblCourseCode.insets = new Insets(0, 0, 5, 5);
        gbc_lblCourseCode.gridx = 1;
        gbc_lblCourseCode.gridy = 0;
        getContentPane().add(lblCourseCode, gbc_lblCourseCode);

        txtCourseCode = new JTextField();
        txtCourseCode.setName("coursecodeTextBox");
        GridBagConstraints gbc_txtCourseCode = new GridBagConstraints();
        gbc_txtCourseCode.insets = new Insets(0, 0, 5, 5);
        gbc_txtCourseCode.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCourseCode.gridx = 2;
        gbc_txtCourseCode.gridy = 0;
        getContentPane().add(txtCourseCode, gbc_txtCourseCode);

        JLabel lblCourseTitle = new JLabel("Course Title");
        GridBagConstraints gbc_lblCourseTitle = new GridBagConstraints();
        gbc_lblCourseTitle.anchor = GridBagConstraints.EAST;
        gbc_lblCourseTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblCourseTitle.gridx = 1;
        gbc_lblCourseTitle.gridy = 1;
        getContentPane().add(lblCourseTitle, gbc_lblCourseTitle);

        txtCourseTitle = new JTextField();
        txtCourseTitle.setName("coursetitleTextBox");
        GridBagConstraints gbc_txtCourseTitle = new GridBagConstraints();
        gbc_txtCourseTitle.insets = new Insets(0, 0, 5, 5);
        gbc_txtCourseTitle.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCourseTitle.gridx = 2;
        gbc_txtCourseTitle.gridy = 1;
        getContentPane().add(txtCourseTitle, gbc_txtCourseTitle);

        JLabel lblInstructorName = new JLabel("Instructor Name");
        GridBagConstraints gbc_lblInstructorName = new GridBagConstraints();
        gbc_lblInstructorName.anchor = GridBagConstraints.EAST;
        gbc_lblInstructorName.insets = new Insets(0, 0, 5, 5);
        gbc_lblInstructorName.gridx = 1;
        gbc_lblInstructorName.gridy = 2;
        getContentPane().add(lblInstructorName, gbc_lblInstructorName);

        txtInstructorName = new JTextField();
        txtInstructorName.setName("instructornameTextBox");
        GridBagConstraints gbc_txtInstructorName = new GridBagConstraints();
        gbc_txtInstructorName.insets = new Insets(0, 0, 5, 5);
        gbc_txtInstructorName.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtInstructorName.gridx = 2;
        gbc_txtInstructorName.gridy = 2;
        getContentPane().add(txtInstructorName, gbc_txtInstructorName);

        JLabel lblCreditHours = new JLabel("Credit Hours");
        GridBagConstraints gbc_lblCreditHours = new GridBagConstraints();
        gbc_lblCreditHours.anchor = GridBagConstraints.EAST;
        gbc_lblCreditHours.insets = new Insets(0, 0, 5, 5);
        gbc_lblCreditHours.gridx = 1;
        gbc_lblCreditHours.gridy = 3;
        getContentPane().add(lblCreditHours, gbc_lblCreditHours);

        txtCreditHours = new JTextField();
        txtCreditHours.setName("credithoursTextBox");
        GridBagConstraints gbc_txtCreditHours = new GridBagConstraints();
        gbc_txtCreditHours.insets = new Insets(0, 0, 5, 5);
        gbc_txtCreditHours.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCreditHours.gridx = 2;
        gbc_txtCreditHours.gridy = 3;
        getContentPane().add(txtCreditHours, gbc_txtCreditHours);

        JLabel lblDescription = new JLabel("Description");
        GridBagConstraints gbc_lblDescription = new GridBagConstraints();
        gbc_lblDescription.anchor = GridBagConstraints.EAST;
        gbc_lblDescription.insets = new Insets(0, 0, 5, 5);
        gbc_lblDescription.gridx = 1;
        gbc_lblDescription.gridy = 4;
        getContentPane().add(lblDescription, gbc_lblDescription);

        txtDescription = new JTextField();
        txtDescription.setName("descriptionTextBox");
        GridBagConstraints gbc_txtDescription = new GridBagConstraints();
        gbc_txtDescription.insets = new Insets(0, 0, 5, 5);
        gbc_txtDescription.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtDescription.gridx = 2;
        gbc_txtDescription.gridy = 4;
        getContentPane().add(txtDescription, gbc_txtDescription);

        btnAdd = new JButton("Add Course");
        btnAdd.setEnabled(false);
        btnAdd.setName("btnAdd");
        btnAdd.addActionListener(e -> new Thread(() -> {
            Course c = buildCourseFromFields();
            if (c != null) {
                courseController.addCourse(c);
            }
        }).start());

        GridBagConstraints gbc_btnAdd = new GridBagConstraints();
        gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
        gbc_btnAdd.gridx = 2;
        gbc_btnAdd.gridy = 5;
        getContentPane().add(btnAdd, gbc_btnAdd);

        btnCancel = new JButton("Cancel");
        btnCancel.setEnabled(false);
        btnCancel.setName("btnCancel");
        btnCancel.addActionListener(e -> {
            clearFields();
            btnUpdateSelected.setEnabled(false);
            btnDeleteSelected.setEnabled(false);
            btnCancel.setEnabled(false);
            listCourses.clearSelection();
            txtCourseCode.setEnabled(true);
        });

        GridBagConstraints gbc_btnCancel = new GridBagConstraints();
        gbc_btnCancel.insets = new Insets(0, 0, 5, 5);
        gbc_btnCancel.gridx = 1;
        gbc_btnCancel.gridy = 5;
        getContentPane().add(btnCancel, gbc_btnCancel);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 6;
        getContentPane().add(scrollPane, gbc_scrollPane);

        listCourseModel = new DefaultListModel<>();
        listCourses = new JList<>(listCourseModel);
        listCourses.setName("courseList");
        listCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listCourses.addListSelectionListener(e -> {
            boolean selected = listCourses.getSelectedIndex() != -1;

            btnDeleteSelected.setEnabled(selected);
            btnUpdateSelected.setEnabled(selected);
            btnCancel.setEnabled(selected);

            if (selected) {
                Course c = listCourses.getSelectedValue();
                txtCourseCode.setText(c.getCourseCode());
                txtCourseTitle.setText(c.getCourseTitle());
                txtInstructorName.setText(c.getInstructorName());
                txtCreditHours.setText(String.valueOf(c.getCreditHours()));
                txtDescription.setText(c.getDescription());

                txtCourseCode.setEnabled(false);
                btnAdd.setEnabled(false);
            } else {
                clearFields();
                txtCourseCode.setEnabled(true);
            }
        });

        listCourses.setCellRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Course c = (Course) value;
                return super.getListCellRendererComponent(list, getDisplayString(c), index, isSelected, cellHasFocus);
            }
        });

        scrollPane.setViewportView(listCourses);

        btnUpdateSelected = new JButton("Update Selected");
        btnUpdateSelected.setEnabled(false);
        btnUpdateSelected.setName("btnUpdateSelected");
        btnUpdateSelected.addActionListener(e -> new Thread(() -> {
            Course c = buildCourseFromFields();
            if (c != null) {
                courseController.updateCourse(c);
            }
        }).start());

        GridBagConstraints gbc_btnUpdateSelected = new GridBagConstraints();
        gbc_btnUpdateSelected.insets = new Insets(0, 0, 5, 5);
        gbc_btnUpdateSelected.gridx = 1;
        gbc_btnUpdateSelected.gridy = 7;
        getContentPane().add(btnUpdateSelected, gbc_btnUpdateSelected);

        btnDeleteSelected = new JButton("Delete Selected");
        btnDeleteSelected.setEnabled(false);
        btnDeleteSelected.setName("btnDeleteSelected");
        btnDeleteSelected.addActionListener(e -> new Thread(() -> {
            Course selectedCourse = listCourses.getSelectedValue();
            if (selectedCourse != null) {
                courseController.deleteCourse(selectedCourse);
            }
        }).start());

        GridBagConstraints gbc_btnDeleteSelected = new GridBagConstraints();
        gbc_btnDeleteSelected.insets = new Insets(0, 0, 5, 5);
        gbc_btnDeleteSelected.gridx = 2;
        gbc_btnDeleteSelected.gridy = 7;
        getContentPane().add(btnDeleteSelected, gbc_btnDeleteSelected);

        lblErrorMessage = new JLabel("");
        lblErrorMessage.setName("errorMessageLabel");
        GridBagConstraints gbc_lblErrorMessage = new GridBagConstraints();
        gbc_lblErrorMessage.gridwidth = 3;
        gbc_lblErrorMessage.insets = new Insets(0, 0, 0, 5);
        gbc_lblErrorMessage.gridx = 0;
        gbc_lblErrorMessage.gridy = 8;
        getContentPane().add(lblErrorMessage, gbc_lblErrorMessage);

        KeyAdapter addEnabler = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean allFilled = !txtCourseCode.getText().trim().isEmpty()
                        && !txtCourseTitle.getText().trim().isEmpty()
                        && !txtInstructorName.getText().trim().isEmpty()
                        && !txtCreditHours.getText().trim().isEmpty()
                        && !txtDescription.getText().trim().isEmpty();

                btnAdd.setEnabled(listCourses.getSelectedIndex() == -1 && allFilled);
            }
        };

        txtCourseCode.addKeyListener(addEnabler);
        txtCourseTitle.addKeyListener(addEnabler);
        txtInstructorName.addKeyListener(addEnabler);
        txtCreditHours.addKeyListener(addEnabler);
        txtDescription.addKeyListener(addEnabler);
    }

    @Override
    public void displayCourses(List<Course> courses) {
        courses.forEach(listCourseModel::addElement);
    }

    @Override
    public void addCourse(Course course) {
        SwingUtilities.invokeLater(() -> {
            listCourseModel.addElement(course);
            resetErrorLabel();
            clearFields();
        });
    }

    @Override
    public void deleteCourse(Course course) {
        SwingUtilities.invokeLater(() -> {
            listCourseModel.removeElement(course);
            resetErrorLabel();
            clearFields();
            txtCourseCode.setEnabled(true);
        });
    }

    @Override
    public void updateCourse(Course course) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < listCourseModel.size(); i++) {
                if (listCourseModel.get(i).getCourseCode().equals(course.getCourseCode())) {
                    listCourseModel.set(i, course);
                    break;
                }
            }
            resetErrorLabel();
            clearFields();
            txtCourseCode.setEnabled(true);
            listCourses.clearSelection();
        });
    }

    @Override
    public void showErrorMessage(String message, Course course) {
        SwingUtilities.invokeLater(() -> {
            String displayMessage = (course == null) ? "" : getDisplayString(course);
            lblErrorMessage.setText(message + displayMessage);
            if (course != null) {
                listCourseModel.removeElement(course);
            }
        });
    }

    private void resetErrorLabel() {
        lblErrorMessage.setText("");
    }

    private void clearFields() {
        txtCourseCode.setText("");
        txtCourseTitle.setText("");
        txtInstructorName.setText("");
        txtCreditHours.setText("");
        txtDescription.setText("");
    }

    private String getDisplayString(Course c) {
        return c.getCourseCode() + " - " + c.getCourseTitle() + " - " + c.getInstructorName()
                + " - " + c.getCreditHours() + " - " + c.getDescription();
    }

    private boolean isCourseCodeValid(String s) {
        if (s == null) {
            return false;
        }
        return s.matches("^[A-Za-z]{3}\\d{3}$");
    }

    private boolean isLettersAndSpaces(String s) {
        if (s == null) {
            return false;
        }
        if (s.isEmpty()) {
            return false;
        }
        return s.matches("^[a-zA-Z ]+$");
    }

    private boolean isNumeric(String s) {
        if (s == null) {
            return false;
        }
        return s.matches("^\\d+$");
    }


    private Course buildCourseFromFields() {
        String code = txtCourseCode.getText().trim();
        String title = txtCourseTitle.getText().trim();
        String instructor = txtInstructorName.getText().trim();
        String creditText = txtCreditHours.getText().trim();
        String description = txtDescription.getText().trim();

        if (!isCourseCodeValid(code)) {
            showErrorMessage("Course Code must be 3 letters followed by 3 digits (e.g., APT001)", null);
            return null;
        }

        if (!isLettersAndSpaces(title)) {
            showErrorMessage("Course Title must contain only letters and spaces", null);
            return null;
        }

        if (!isLettersAndSpaces(instructor)) {
            showErrorMessage("Instructor Name must contain only letters and spaces", null);
            return null;
        }

        if (!isNumeric(creditText)) {
            showErrorMessage("Credit Hours must be a number", null);
            return null;
        }

        if (description.length() > DESCRIPTION_MAX_LENGTH) {
            showErrorMessage("Description cannot exceed 200 characters", null);
            return null;
        }

        int credits = Integer.parseInt(creditText);
        return new Course(code, title, instructor, credits, description);
    }
}
