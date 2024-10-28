package com.swpuwyp.saexperiment332.controller;

import com.swpuwyp.saexperiment332.common.Result.Result;
import com.swpuwyp.saexperiment332.dto.ContactDTO;
import com.swpuwyp.saexperiment332.dto.ContactUpdateDTO;
import com.swpuwyp.saexperiment332.entity.Contact;
import com.swpuwyp.saexperiment332.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;


// 控制器层
@CrossOrigin(origins = "http://localhost:8080") // 允许来自 http://localhost:8080 的跨域请求
@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    // 添加联系人
    @PostMapping("/add")
    public Result<?> addContact(@RequestBody ContactDTO contactDTO) {
        String result = contactService.addContact(contactDTO.getName(), contactDTO.getPhone());
        return result.contains("成功")
                ? new Result<>().success().put(result)
                : new Result<>().error(result);
    }

    // 删除联系人
    @PostMapping("/delete")
    public Result<?> deleteContact(@RequestBody ContactDTO contactDTO) {
        String result = contactService.deleteContact(contactDTO.getName(), contactDTO.getPhone());
        return result.contains("成功")
                ? new Result<>().success().put(result)
                : new Result<>().error(result);
    }

    // 查询联系人
    @PostMapping("/query")
    public Result<?> queryContact(@RequestBody ContactDTO contactDTO) {
        StringBuilder sb = new StringBuilder();
        String name = contactDTO.getName();
        String phone = contactDTO.getPhone();
        System.out.println(name);
        try {
            List<Contact> contacts = contactService.queryContacts(name != null ? name : "", phone != null ? phone : "");
            for (Contact contact : contacts) {
                sb.append("姓名：").append(contact.getName())
                        .append(", 电话：").append(contact.getPhoneNum()).append("\n");
            }
        } catch (SQLException e) {
            return new Result<>().error("查询失败：" + e.getMessage());
        }

        String result = sb.length() > 0 ? sb.toString() : "没有找到联系人。";
        return new Result<>().success().put(result);
    }

    // 修改联系人信息
    @PostMapping("/modify")
    public Result<?> modifyContact(@RequestBody ContactUpdateDTO contactUpdateDTO) {
        System.out.println(contactUpdateDTO.getOldInfo() + "  " + contactUpdateDTO.getNewName() + "  " + contactUpdateDTO.getNewPhoneNum());
        String result = contactService.modifyContact(contactUpdateDTO.getOldInfo(),
                contactUpdateDTO.getNewName(), contactUpdateDTO.getNewPhoneNum());

        return result.contains("成功")
                ? new Result<>().success().put(result)
                : new Result<>().error(result);
    }
}

