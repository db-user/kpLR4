// Book.kt
class Book(
    val title: String,
    val author: String,
    val isbn: String,
    var available: Boolean = true,
    var borrower: Member? = null
) {
    private val observers: MutableList<Observer> = mutableListOf()

    fun isAvailable(): Boolean = available

    fun borrow(member: Member) {
        if (available) {
            available = false
            borrower = member
            member.borrow_book(this)
        } else {
            println("Book is not available.")
        }
    }

    fun returnBook() {
        available = true
        borrower?.return_book(this)
        borrower = null
        notifyObservers()
    }

    fun addObserver(observer: Observer) {
        observers.add(observer)
    }

    fun notifyObservers() {
        for (observer in observers) {
            observer.update(this)
        }
    }
}

// Member.kt
interface Observer {
    fun update(book: Book)
}

class Member(
    val name: String,
    val memberId: String,
    val maxBooks: Int = 3
) : Observer {
    val borrowedBooks: MutableList<Book> = mutableListOf()

    fun borrow_book(book: Book) {
        if (can_borrow()) {
            borrowedBooks.add(book)
        } else {
            println("$name can't borrow more books.")
        }
    }

    fun return_book(book: Book) {
        borrowedBooks.remove(book)
    }

    fun can_borrow(): Boolean = borrowedBooks.size < maxBooks

    override fun update(book: Book) {
        if (book.isAvailable()) {
            println("The book '${book.title}' is now available.")
        }
    }
}

// Library.kt
interface LibraryComponent {
    fun getBooksCount(): Int
    fun getAvailableBooksCount(): Int
}

class Library : LibraryComponent {
    val books: MutableList<Book> = mutableListOf()
    val members: MutableList<Member> = mutableListOf()

    fun add_book(book: Book) {
        books.add(book)
    }

    fun remove_book(book: Book) {
        books.remove(book)
    }

    fun find_book_by_title(title: String): Book? {
        return books.find { it.title == title }
    }

    fun register_member(member: Member) {
        members.add(member)
    }

    fun unregister_member(member: Member) {
        members.remove(member)
    }

    fun borrow_book(member: Member, title: String) {
        val book = find_book_by_title(title)
        if (book != null && book.isAvailable() && member.can_borrow()) {
            book.borrow(member)
        } else {
            println("Cannot borrow book '$title'.")
        }
    }

    fun return_book(member: Member, title: String) {
        val book = find_book_by_title(title)
        if (book != null) {
            book.returnBook()
        } else {
            println("Book '$title' not found.")
        }
    }

    override fun getBooksCount(): Int = books.size

    override fun getAvailableBooksCount(): Int = books.count { it.isAvailable() }

    fun get_books_count_by_title(title: String): Int {
        return books.count { it.title == title }
    }

    fun get_books_count_by_author(author: String): Int {
        return books.count { it.author == author }
    }
}

// Librarian.kt
class Librarian(
    val name: String,
    val employeeId: String
) {
    fun add_book_to_library(book: Book, library: Library) {
        library.add_book(book)
    }

    fun remove_book_from_library(book: Book, library: Library) {
        library.remove_book(book)
    }

    fun register_new_member(member: Member, library: Library) {
        library.register_member(member)
    }
}

// BookBuilder.kt
class BookBuilder {
    private var title: String = ""
    private var author: String = ""
    private var isbn: String = ""
    private var available: Boolean = true
    private var borrower: Member? = null

    fun setTitle(title: String): BookBuilder {
        this.title = title
        return this
    }

    fun setAuthor(author: String): BookBuilder {
        this.author = author
        return this
    }

    fun setIsbn(isbn: String): BookBuilder {
        this.isbn = isbn
        return this
    }

    fun setAvailable(available: Boolean): BookBuilder {
        this.available = available
        return this
    }

    fun setBorrower(borrower: Member?): BookBuilder {
        this.borrower = borrower
        return this
    }

    fun build(): Book {
        return Book(title, author, isbn, available, borrower)
    }
}

// Main.kt
fun main() {
    val library = Library()
    val librarian = Librarian("John Doe", "L123")
    val member = Member("Alice", "M001")

    librarian.register_new_member(member, library)

    // Використовуємо Builder для створення книг
    val book1 = BookBuilder()
        .setTitle("Kotlin Programming")
        .setAuthor("JetBrains")
        .setIsbn("123-456-789")
        .build()
    val book2 = BookBuilder()
        .setTitle("Advanced Kotlin")
        .setAuthor("JetBrains")
        .setIsbn("987-654-321")
        .build()

    librarian.add_book_to_library(book1, library)
    librarian.add_book_to_library(book2, library)

    // Підписуємо користувача на спостереження за книгами
    book1.addObserver(member)
    book2.addObserver(member)

    // Alice borrows a book
    library.borrow_book(member, "Kotlin Programming")

    // Alice returns the book, спостерігачі отримують сповіщення
    library.return_book(member, "Kotlin Programming")
}