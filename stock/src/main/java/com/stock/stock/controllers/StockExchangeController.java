package com.stock.stock.controllers;

import com.stock.stock.models.Order;
import com.stock.stock.models.Transaction;
import com.stock.stock.services.StockExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-exchange")
public class StockExchangeController {
    private final StockExchangeService stockExchangeService;

    public StockExchangeController(StockExchangeService stockExchangeService) {
        this.stockExchangeService = stockExchangeService;
    }

    @PostMapping("/buy")
    public ResponseEntity<String> placeBuyOrder(@RequestBody Order order) {
        stockExchangeService.placeBuyOrder(order);
        return ResponseEntity.ok("Buy order placed successfully.");
    }

    @PostMapping("/sell")
    public ResponseEntity<String> placeSellOrder(@RequestBody Order order) {
        stockExchangeService.placeSellOrder(order);
        return ResponseEntity.ok("Sell order placed successfully.");
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactions() {
        return ResponseEntity.ok(stockExchangeService.getTransactions());
    }
}