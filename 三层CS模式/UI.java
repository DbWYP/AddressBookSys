package 软件体系结构实验三.三层CS模式;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

public class UI {
    private ContactService contactService;
    private JTextArea displayArea;
    private JTextField nameField, phoneField;

    public UI(ContactService contactService) {
        this.contactService = contactService;

        JFrame frame = new JFrame("个人通讯录系统");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.append("欢迎使用个人通讯录管理系统！\n"
                + "添加用户请输入完整正确的姓名和电话号码！\n"
                + "查询用户支持姓名模糊查询，也支持指定手机号查询！\n"
                + "修改用户信息请输入完整正确的姓名或手机号码！\n"
                + "删除用户请输入被删除的联系人完整的姓名和电话号码！\n\n");

        frame.add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("姓名"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("电话"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);
        frame.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加");
        JButton deleteButton = new JButton("删除");
        JButton queryButton = new JButton("查询");
        JButton exitButton = new JButton("退出");
        JButton modifyButton = new JButton("修改");

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(queryButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(exitButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addContact());
        deleteButton.addActionListener(e -> deleteContact());
        queryButton.addActionListener(e -> queryContact());
        modifyButton.addActionListener(e -> openModifyWindow());
        exitButton.addActionListener(e -> {
            contactService.close();
            System.exit(0);
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // 添加用户
    private void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String result = contactService.addContact(name, phone);
        displayArea.append(result + "\n");
        clearFields();
    }

    // 删除用户
    private void deleteContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String result = contactService.deleteContact(name, phone);
        displayArea.append(result + "\n");
        clearFields();
    }

    // 查询用户
    private void queryContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        ResultSet resultSet = contactService.queryContacts(name, phone);
        displayResults(resultSet);
        clearFields();
    }

    // 创建询问修改的窗口
    private void openModifyWindow() {
        String oldInfo = JOptionPane.showInputDialog(null, "请输入要修改的联系人姓名或电话号码：");
        if (oldInfo == null || oldInfo.isEmpty()) {
            displayArea.append("请输入联系人姓名或电话号码！\n");
            return;
        }

        ResultSet resultSet = contactService.queryContactByNameOrPhone(oldInfo);
        if (resultSet != null && displayContactInfo(resultSet)) {
            createModifyWindow(oldInfo, resultSet);
        } else {
            displayArea.append("未找到相关联系人。\n");
        }
    }

    // 创建修改具体信息的界面
    private void createModifyWindow(String oldInfo, ResultSet resultSet) {
        try {
            String oldName = resultSet.getString("name");
            String oldPhone = resultSet.getString("phone_num");

            JFrame modifyFrame = new JFrame("修改联系人信息");
            modifyFrame.setSize(400, 300);
            modifyFrame.setLayout(new BorderLayout());

            JPanel modifyPanel = new JPanel(new GridLayout(3, 2));
            modifyPanel.add(new JLabel("旧姓名:"));
            JTextField nameField = new JTextField(oldName);
            modifyPanel.add(nameField);

            modifyPanel.add(new JLabel("旧电话:"));
            JTextField phoneField = new JTextField(oldPhone);
            modifyPanel.add(phoneField);

            JButton submitButton = new JButton("提交修改");
            modifyFrame.add(modifyPanel, BorderLayout.CENTER);
            modifyFrame.add(submitButton, BorderLayout.SOUTH);

            modifyFrame.setLocationRelativeTo(null);
            modifyFrame.setVisible(true);

            submitButton.addActionListener(e -> {
                String newName = nameField.getText();
                String newPhone = phoneField.getText();
                String result = contactService.modifyContact(oldInfo, newName, newPhone);
                displayArea.append(result + "\n");
                modifyFrame.dispose();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取联系人信息并将其显示
    private boolean displayContactInfo(ResultSet resultSet) {
        try {
            if (resultSet.next()) {// 移动光标到 ResultSet 中的下一行，如果存在数据则返回true
                displayArea.append("姓名: " + resultSet.getString("name") +
                        " 电话: " + resultSet.getString("phone_num") + "\n");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 显示查询到的结果
    private void displayResults(ResultSet resultSet) {
        try {
            boolean hasResults = false;
            if (resultSet != null) {
                while (resultSet.next()) {
                    hasResults = true;
                    displayArea.append("姓名: " + resultSet.getString("name") +
                            " 电话: " + resultSet.getString("phone_num") + "\n");
                }
            }
            if (!hasResults) {
                displayArea.append("没有找到相关联系人！请检查姓名或电话是否有误！\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 清空输入框中的所有内容
    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
    }

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager("jdbc:mysql://localhost:3306/cs2", "root", "123456");
        ContactService contactService = new ContactService(dbManager);
        new UI(contactService);
    }
}
