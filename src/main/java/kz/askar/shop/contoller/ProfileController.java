package kz.askar.shop.contoller;

import kz.askar.shop.entity.Category;
import kz.askar.shop.entity.Order;
import kz.askar.shop.entity.User;
import kz.askar.shop.service.CategoryService;
import kz.askar.shop.service.OrderService;
import kz.askar.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @RequestMapping("")
    public String profile(Model model) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return "redirect:/login";
        }

        List<Category> categories = categoryService.findAll();
        List<Order> orders = orderService.findByUser(user);

        model.addAttribute("categories", categories);
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);

        return "view/data/profile_view";
    }
}
