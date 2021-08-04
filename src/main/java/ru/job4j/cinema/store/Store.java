package ru.job4j.cinema.store;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.List;

public interface Store {

    Account save(Account account);

    boolean save(List<Ticket> tickets);

    Collection<Ticket> findAllTickets();

    Ticket findTicket(Ticket ticket);

    Account findAccountByEmail(String email);

    void update(Ticket ticket);
}
