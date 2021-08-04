package ru.job4j.cinema.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.PsqlStore;
import ru.job4j.cinema.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PaymentServlet extends HttpServlet {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("payment.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Store store = PsqlStore.instOf();
        String arr = req.getParameter("arr");
        String[] arrayFromJson = arr.split(",");
        int sessionId = Integer.parseInt(req.getParameter("sessionId"));
        String username = req.getParameter("username");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        Account account = Account.of(username, phone, email);
        Account checked = store.findAccountByEmail(account.getEmail());
        if (checked.getUsername() == null) {
            account = store.save(account);
        } else {
            account = checked;
        }
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < arrayFromJson.length; i++) {
            int row = Integer.parseInt(arrayFromJson[i].split("-")[0]);
            int cell = Integer.parseInt(arrayFromJson[i].split("-")[1]);
            tickets.add(new Ticket(row, cell, sessionId, account.getId()));
        }
        String answer;
        if (store.save(tickets)) {
            answer = "Успешно забронировано";
        } else {
            answer = "Место уже занято";
        }

        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        String json = GSON.toJson(answer);
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }
}
