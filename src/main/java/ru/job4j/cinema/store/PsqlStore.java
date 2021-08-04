package ru.job4j.cinema.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.Ticket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.*;

public class PsqlStore implements Store {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private final BasicDataSource pool = new BasicDataSource();

    private PsqlStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            LOG.error("Exception ", e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            LOG.error("Exception ", e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new PsqlStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Ticket> findAllTickets() {
        List<Ticket> result = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "SELECT * FROM ticket")
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Ticket(
                            rs.getInt("id"),
                            rs.getInt("session_id"),
                            rs.getInt("row"),
                            rs.getInt("cell"),
                            rs.getInt("account_id")
                    ));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception ", e);
        }
        return result;
    }

    @Override
    public Ticket findTicket(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM ticket WHERE row = ? AND cell = ? AND session_id = ?"
             )) {
            ps.setInt(1, ticket.getRow());
            ps.setInt(2, ticket.getCell());
            ps.setInt(3, ticket.getSessionId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt("id"));
                    ticket.setSessionId(rs.getInt("session_id"));
                    ticket.setRow(rs.getInt("row"));
                    ticket.setCell(rs.getInt("cell"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception ", e);
        }
        return ticket;
    }

    @Override
    public boolean save(List<Ticket> tickets) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "INSERT INTO ticket(session_id, row, cell, account_id) VALUES (?, ?, ?, ?)"
             )
        ) {
            cn.setAutoCommit(false);
            for (Ticket ticket: tickets) {
                ps.setInt(1, ticket.getSessionId());
                ps.setInt(2, ticket.getRow());
                ps.setInt(3, ticket.getCell());
                ps.setInt(4, ticket.getAccountId());
                ps.addBatch();
            }
            int[] updateCounts = ps.executeBatch();
            if (Arrays.stream(updateCounts).anyMatch(c -> c == 0)) {
                return false;
            }
            cn.commit();
            return true;
        } catch (Exception e) {
            LOG.error("An exception occurred: ", e);
        }
        return false;
    }

    @Override
    public Account save(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )
        ) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    account.setId(id.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception ", e);
        }
        return account;
    }

    @Override
    public Account findAccountByEmail(String email) {
        Account account = null;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM account WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                account = new Account();
                if (rs.next()) {
                    account.setId(rs.getInt("id"));
                    account.setUsername(rs.getString("username"));
                    account.setEmail(rs.getString("email"));
                    account.setPhone(rs.getString("phone"));
                }
            }
        } catch (SQLException e) {
            LOG.error("Exception ", e);
        }
        return account;
    }

    @Override
    public void update(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(
                     "UPDATE ticket SET account_id = ? WHERE row = ? and cell = ? and session_id = ?")
        ) {
            ps.setInt(1, ticket.getAccountId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getSessionId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("An exception occurred: ", e);
        }
    }

}
