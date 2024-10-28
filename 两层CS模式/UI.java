package 软件体系结构实验三.两层CS模式2版;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

public class UI {
    private DatabaseManager dbManager;
    private JTextArea displayArea;
    private JTextField nameField, phoneField;

    public UI(DatabaseManager dbManager) {
        this.dbManager = dbManager;

        JFrame frame = new JFrame("个人通讯录系统");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);

        // 添加系统使用说明
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
            dbManager.close();
            System.exit(0);
        });

        frame.setLocationRelativeTo(null);// 将窗口设置在中央
        frame.setVisible(true);// 将窗口设置为可见
    }

    private void addContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        if (!name.isEmpty() && !phone.isEmpty()) {
            if(isValidPhoneNumber(phone)) {
                dbManager.addContact(name, phone);
                displayArea.append("联系人添加成功\n姓名：" + name + "\n电话：" + phone + "\n");
            }else {
                System.out.println("\n电话号码格式不正确，请输入11位数字！\n");
            }
        } else {
            displayArea.append("\n请填写完整信息！\n");
        }
        // 添加成功后把文本框内容清空
        nameField.setText("");
        phoneField.setText("");
    }

    private void deleteContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        if (!name.isEmpty() && !phone.isEmpty()) {
            if (isValidPhoneNumber(phone)) {
                boolean deleted = dbManager.deleteContact(name, phone); // 假设 dbManager.deleteContact 返回删除是否成功的布尔值
                if (deleted) {
                    displayArea.append("联系人删除成功\n姓名：" + name + "\n电话：" + phone + "\n");
                } else {
                    displayArea.append("未找到联系人，无法删除。\n");
                }
            } else {
                displayArea.append("\n电话号码格式不正确，请输入11位数字！\n");
            }
        } else {
            displayArea.append("\n请填写完整信息！\n");
        }
        // 添加成功后把文本框内容清空
        nameField.setText("");
        phoneField.setText("");
    }

    private void queryContact() {
        String name = nameField.getText();
        String phone = phoneField.getText();

        boolean hasResults = false; // 用于检测是否有查询结果
        ResultSet resultSet = null;

        // 如果输入了姓名，执行模糊查询
        if (!name.isEmpty()) {
            resultSet = dbManager.queryContactByName(name); // 使用模糊查询方法
            displayArea.append("\n按姓名模糊查询结果:\n");

            // 如果输入了手机号，执行精确查询
        } else if (!phone.isEmpty()) {
            if (isValidPhoneNumber(phone)) {
                resultSet = dbManager.queryContactByPhone(phone); // 使用精确查询方法
                displayArea.append("\n按手机号精准查询结果:\n");
            } else {
                displayArea.append("电话号码格式不正确，请输入11位数字！\n");
            }

            // 如果什么都没输入，查询所有联系人
        } else {
            resultSet = dbManager.queryAllContacts(); // 查询所有联系人
            displayArea.append("\n所有联系人:\n");
        }

        // 输出查询结果
        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    hasResults = true;
                    displayArea.append("姓名: " + resultSet.getString("name") +
                            " 电话: " + resultSet.getString("phone_num") + "\n");
                }
            }if(!hasResults){
                displayArea.append("没有找到相关联系人。\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        nameField.setText("");
        phoneField.setText("");
    }

    private void openModifyWindow() {
        // 弹出一个对话框要求用户输入要修改的联系人姓名或电话
        String oldInfo = JOptionPane.showInputDialog(null, "请输入要修改的联系人姓名或电话号码：");

        if (oldInfo == null || oldInfo.isEmpty()) {
            displayArea.append("请输入联系人姓名或电话号码！\n");
            return;
        }

        // 根据姓名或电话号码查询联系人信息
        ResultSet resultSet = dbManager.queryContactByNameOrPhone(oldInfo);// oldInfo是在对话框中输入的查询信息

        try {
            if (resultSet != null && resultSet.next()) {
                // 获取查询到的旧信息
                String oldName = resultSet.getString("name");
                String oldPhone = resultSet.getString("phone_num");

                // 创建修改窗口并预填充旧信息
                JFrame modifyFrame = new JFrame("修改联系人信息");
                modifyFrame.setSize(400, 300);
                modifyFrame.setLayout(new BorderLayout());

                JPanel modifyPanel = new JPanel(new GridLayout(3, 2));
                modifyPanel.add(new JLabel("旧姓名:"));
                JTextField nameField = new JTextField(oldName); // 预填充旧姓名，利用JTextField的特征：可以修改其中的内容
                modifyPanel.add(nameField);

                modifyPanel.add(new JLabel("旧电话:"));
                JTextField phoneField = new JTextField(oldPhone); // 预填充旧电话，利用JTextField的特征：可以修改其中的内容
                modifyPanel.add(phoneField);

                JButton submitButton = new JButton("提交修改");
                modifyFrame.add(modifyPanel, BorderLayout.CENTER);
                modifyFrame.add(submitButton, BorderLayout.SOUTH);

                // 设置窗口位置居中
                modifyFrame.setLocationRelativeTo(null);
                modifyFrame.setVisible(true);

                // 提交修改按钮的事件处理
                submitButton.addActionListener(e -> {
                    String newName = nameField.getText();
                    String newPhone = phoneField.getText();

                    if (!newName.isEmpty() && isValidPhoneNumber(newPhone)) {
                        boolean success = dbManager.modifyContact(oldInfo, newName, newPhone);
                        if (success) {
                            displayArea.append("联系人信息修改成功！\n");
                        } else {
                            displayArea.append("修改失败，未找到联系人。\n");
                        }
                        modifyFrame.dispose(); // 修改完成后关闭窗口
                    } else {
                        displayArea.append("请输入有效的姓名和电话号码！\n");
                    }
                });

            } else {
                displayArea.append("未找到相关联系人。\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{11}"); // 正则表达式验证 11 位数字
    }

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager("jdbc:mysql://localhost:3306/cs2", "root", "123456");
        new UI(dbManager);
    }
}
