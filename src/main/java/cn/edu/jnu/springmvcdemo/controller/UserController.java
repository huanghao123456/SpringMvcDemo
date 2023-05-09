package cn.edu.jnu.springmvcdemo.controller;

import cn.edu.jnu.springmvcdemo.vo.User;
import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Controller
public class UserController {

    @RequestMapping("/login")
    public String login(User user, Model model, HttpSession session) {
        if ("kongchuiyun".equals(user.getUserName()) && "123".equals(user.getPassWord())) {
            session.setAttribute("loginUser", user);
            return "redirect:/upload.html";
        }
        model.addAttribute("loginFailedMsg", "账号密码错误！");
        return "login";
    }

    @RequestMapping("/upload")
    public void uploadFile(MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("uploadMsg", "上传失败，请选择文件！");
        } else {
            try {
                String filename = file.getOriginalFilename();
                // 存放的位置为./SpringMvcDemo/target/classes/static/
                String filePath = ResourceUtils.getURL("classpath:").getPath() + "static/";
                String uniqueNamePrefix = LocalDateTime.now().toString().replaceAll("(\\.|:)", "-");
                //时间戳分类文件
                String realPath = filePath + uniqueNamePrefix + "_" + filename;
                File dest = new File(realPath);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();//新建文件夹 多级目录
                }

                file.transferTo(dest);

                model.addAttribute("uploadMsg", "文件上传成功！");
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("uploadMsg", "文件上传失败！");
            }
        }
    }

    @GetMapping("/goDownload")
    public String goDownload(Model model) throws FileNotFoundException {
        String filePath = ResourceUtils.getURL("classpath:").getPath() + "static/";
        File file = new File(filePath);
        //判断文件夹是否存在
        if (file.exists()) {
            //获取文件夹下面的所有名称
            String[] list = file.list();
            model.addAttribute("fileNames", list);
        }

        return "download";
    }

    @GetMapping("/download/hutool")
    @ResponseBody
    public void downloadByHutool(@RequestParam(value = "fileName") String fileName,
                                 HttpServletResponse response) throws FileNotFoundException {
        //防止中文乱码
        response.setCharacterEncoding("UTF-8");
        String filePath = ResourceUtils.getURL("classpath:").getPath() + "static/";
        ServletUtil.write(response,new File(filePath + fileName));
    }

}
