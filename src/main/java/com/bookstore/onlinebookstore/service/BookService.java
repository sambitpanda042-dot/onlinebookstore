package com.bookstore.onlinebookstore.service;

import com.bookstore.onlinebookstore.entity.Book;
import com.bookstore.onlinebookstore.exception.BookNotFoundException;
import com.bookstore.onlinebookstore.exception.OutOfStockException;
import com.bookstore.onlinebookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
    }

    // THE BUY BUSINESS LOGIC
    @Transactional // Guarantees database safety during data edits
    public Book purchaseBook(Long id) {
        // 1. Locate the book or throw our clean 404 error
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));

        // 2. Validate inventory count is greater than zero
        if (book.getQuantity() <= 0) {
            throw new OutOfStockException("Sorry, '" + book.getTitle() + "' is out of stock!");
        }

        // 3. Drop inventory number down by exactly 1 item
        book.setQuantity(book.getQuantity() - 1);

        // 4. Save the adjusted stock value record back to the system
        return bookRepository.save(book);
    }
}