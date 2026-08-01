package com.example.demo;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// 1. 데이터 모델 (Entity)
@Entity
@Table(name = "MY_USERS")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public User() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

// 2. DB 조작 도구 (Repository)
interface UserRepository extends JpaRepository<User, Long> {}

// 3. 통합 컨트롤러
@Controller // 화면(HTML)을 돌려주기 위해 기본은 @Controller로 설정
public class HelloController {

    private final UserRepository userRepository;

    public HelloController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --- UI 전용 (HTML을 리턴) ---
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "index"; // templates/index.html 파일을 찾아서 보여줌
    }

    // --- 데이터 전용 (문자열이나 JSON을 리턴할 땐 @ResponseBody를 꼭 붙여야 함) ---

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "서버 연결 성공! 송찬영 천재 인증.";
    }

    @GetMapping("/all")
    @ResponseBody
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    // 유저 추가 (알림창 띄우기 버전)
    @GetMapping("/add")
    @ResponseBody
    public String addUser(@RequestParam String name) {
        User user = new User();
        user.setName(name);
        userRepository.save(user);

        // 자바스크립트를 리턴해서 알림창 띄우고 다시 /로 보냄
        return "<script>" +
                "alert('" + name + "님이 추가되었습니다!');" +
                "location.href='/';" +
                "</script>";
    }

    // 유저 삭제 (알림창 띄우기 버전)
    @GetMapping("/delete")
    @ResponseBody
    public String deleteUser(@RequestParam Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "<script>" +
                    "alert('ID " + id + "번 유저가 삭제되었습니다.');" +
                    "location.href='/';" +
                    "</script>";
        }
        return "<script>alert('대상 없음'); location.href='/';</script>";
    }

    // 유저 수정 (알림창 띄우기 버전)
    @GetMapping("/update")
    @ResponseBody
    public String updateUser(@RequestParam Long id, @RequestParam String name) {
        return userRepository.findById(id).map(user -> {
            user.setName(name);
            userRepository.save(user);
            return "<script>alert('수정 완료!'); location.href='/';</script>";
        }).orElse("<script>alert('실패'); location.href='/';</script>");
    }
}