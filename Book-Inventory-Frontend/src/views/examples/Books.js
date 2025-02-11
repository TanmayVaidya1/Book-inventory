import {
  Card,
  CardHeader,
  CardFooter,
  Pagination,
  PaginationItem,
  PaginationLink,
  Table,
  Container,
  Row,
  Modal,
  Button,
  Input,
  Form,
  FormGroup,
  Label,
  InputGroup,
  InputGroupAddon,
  InputGroupText,
  Spinner
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import Swal from 'sweetalert2';

const getUserIdFromToken = (token) => {
  try {
    const decoded = jwtDecode(token); // Use jwtDecode instead of jwt_decode
    return decoded.userId; // Assuming the `userId` is stored in the payload of the JWT
  } catch (error) {
    console.error("Invalid token", error);
    return null;
  }
};

const Books = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedBook, setSelectedBook] = useState(null);
  const [authors, setAuthors] = useState([]);
  const [categories, setCategories] = useState([]);

  const [searchTerm, setSearchTerm] = useState("");
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');




  // Filter books based on search term
  const filteredBooks = books.filter((book) => {
    const search = searchTerm.toLowerCase();
    return (
      book.title.toLowerCase().includes(search) ||
      book.isbn.toLowerCase().includes(search) ||
      book.authorName?.toLowerCase().includes(search) ||
      book.categoryName?.toLowerCase().includes(search) ||
      book.description?.toLowerCase().includes(search)
    );
  });

  useEffect(() => {
    const fetchAuthorsAndCategories = async () => {
      try {
        const token = localStorage.getItem("authToken");

        const [authorsRes, categoriesRes] = await Promise.all([
          fetch("http://localhost:8083/api/authors", {
            headers: { "Authorization": `Bearer ${token}` },
          }),
          fetch("http://localhost:8083/api/categories", {
            headers: { "Authorization": `Bearer ${token}` },
          }),
        ]);

        if (!authorsRes.ok || !categoriesRes.ok) {
          throw new Error("Failed to fetch authors or categories");
        }

        setAuthors(await authorsRes.json());
        setCategories(await categoriesRes.json());
      } catch (error) {
        console.error("Error fetching authors or categories:", error);
        setError(error.message);
      }
    };

    const fetchBooks = async () => {
      const token = localStorage.getItem("authToken");
      const userId = getUserIdFromToken(token); // Assuming this function extracts user ID from the token
      try {
        const response = await fetch(`http://localhost:8083/api/books/user/${userId}`, {
          headers: { "Authorization": `Bearer ${token}` },
        });

        if (!response.ok) {
          throw new Error('Error fetching books');
        }

        const data = await response.json();
        setBooks(data);  // Store books data in state
      } catch (err) {
        setError(err.message);
        console.error(err);
      } finally {
        setLoading(false);  // Stop loading state after books are fetched
      }
    };

    fetchAuthorsAndCategories();
    fetchBooks();
  }, []);



  const [formData, setFormData] = useState({
    title: '',
    isbn: '',
    price: '',
    quantity: '',
    description: '',
    // authorName: '', // Add this
    // categoryName: '' // Add this
  });


  // const toggleModal = (book = null) => {
  //   setSelectedBook(book);
  //   setFormData({
  //     title: book ? book.title : '',
  //     isbn: book ? book.isbn : '',
  //     price: book ? book.price : '',
  //     quantity: book ? book.quantity : '',
  //     description: book ? book.description : ''
  //   });
  //   setModalOpen(!modalOpen);
  // };
  const toggleModal = (book = null) => {
    setSelectedBook(book);
    setFormData({
      title: book ? book.title : '',
      isbn: book ? book.isbn : '',
      price: book ? book.price : '',
      quantity: book ? book.quantity : '',
      description: book ? book.description : '',
      // authorName: book ? book.authorName : '',  // Set authorId
      // categoryName: book ? book.categoryName : '', // Set categoryId
    });
    setModalOpen(!modalOpen);
  };

  const [exampleModal2, setExampleModal2] = useState(false);

  // Renamed toggle function to toggleModal2
  const toggleModal2 = () => {
    setExampleModal2(!exampleModal2);
  };

  // const handleInputChange = (e) => {
  //   const { name, value } = e.target;
  //   setFormData({
  //     ...formData,
  //     [name]: value
  //   });
  // };

  // const handleInputChange = (e) => {
  //   const { name, value } = e.target;

  //   // Check if the value is a number and non-negative
  //   if (name === 'price' || name === 'quantity') {
  //     // Ensure value is non-negative and numeric
  //     const numericValue = value >= 0 ? value : 0; // You can replace 0 with any default value you prefer
  //     setFormData({
  //       ...formData,
  //       [name]: numericValue,
  //     });
  //   } else {
  //     setFormData({
  //       ...formData,
  //       [name]: value,
  //     });
  //   }
  // };

   

    // const handleFileChange = (event) => {
    //   setFile(event.target.files[0]);
    //   setMessage("");
    //   setError("");
    // };
  
    // const handleUpload = async (event) => {
    //   event.preventDefault(); // Prevent default form submission
  
    //   if (!file) {
    //     setError("Please select a file first.");
    //     return;
    //   }
  
    //   const token = localStorage.getItem("authToken");
    //   if (!token) {
    //     setError("You are not authenticated. Please log in.");
    //     return;
    //   }
  
    //   const formData = new FormData();
    //   formData.append("file", file);
  
    //   setLoading(true);
    //   setMessage("");
    //   setError("");
  
    //   try {
    //     const response = await fetch("http://localhost:8083/api/books/upload", {
    //       method: "POST",
    //       headers: {
    //         Authorization: `Bearer ${token}`, // Pass token in headers
    //       },
    //       body: formData,
    //     });
  
    //     const result = await response.json();
    //     if (response.ok) {
    //       setMessage(result.message || "File uploaded successfully!");
    //       setFile(null); // Reset file input after successful upload
    //     } else {
    //       setError(result.message || "Error uploading file.");
    //     }

    //      // Close the modal and refresh the page after successful upload
    //   // toggleModal2();
    //   // window.location.reload();  // Refresh the page
    //   } catch (error) {
    //     setError("Error uploading file: " + error.message);
    //   } finally {
    //     setLoading(false);
    //   }
    // };

    const handleFileChange = (event) => {
      setFile(event.target.files[0]);
      setMessage("");
      setError("");
    };
  
    // const handleUpload = async (event) => {
    //   event.preventDefault(); // Prevent default form submission
  
    //   if (!file) {
    //     setError("Please select a file first.");
    //     return;
    //   }
  
    //   const token = localStorage.getItem("authToken");
    //   if (!token) {
    //     setError("You are not authenticated. Please log in.");
    //     return;
    //   }
  
    //   const formData = new FormData();
    //   formData.append("file", file);
  
    //   setLoading(true);
    //   setMessage("");
    //   setError("");
  
    //   try {
    //     const response = await fetch("http://localhost:8083/api/books/upload", {
    //       method: "POST",
    //       headers: {
    //         Authorization: `Bearer ${token}`, // Pass token in headers
    //       },
    //       body: formData,
    //     });
  
    //     const result = await response.json();
    //     if (response.ok) {
    //       setMessage(result.message || "File uploaded successfully!");
    //       setFile(null); // Reset file input after successful upload
          
    //       // SweetAlert2 success alert
    //       Swal.fire({
    //         icon: 'success',
    //         title: 'Success!',
    //         text: result.message || 'File uploaded successfully!',
    //         confirmButtonText: 'OK',
    //       });
  
    //       // Close the modal after successful upload
    //       toggleModal2();
    //     } else {
    //       setError(result.message || "Error uploading file.");
          
    //       // SweetAlert2 error alert
    //       Swal.fire({
    //         icon: 'error',
    //         title: 'Error!',
    //         text: result.message || 'Error uploading file.',
    //         confirmButtonText: 'OK',
    //       });
    //     }
    //   } catch (error) {
    //     setError("Error uploading file: " + error.message);
        
    //     // SweetAlert2 error alert in case of exception
    //     Swal.fire({
    //       icon: 'error',
    //       title: 'Error!',
    //       text: "Error uploading file: " + error.message,
    //       confirmButtonText: 'OK',
    //     });
    //   } finally {
    //     setLoading(false);
    //   }
    // };



    const handleUpload = async (event) => {
      event.preventDefault(); // Prevent default form submission
    
      if (!file) {
        setError("Please select a file first.");
        return;
      }
    
      const token = localStorage.getItem("authToken");
      if (!token) {
        setError("You are not authenticated. Please log in.");
        return;
      }
    
      const formData = new FormData();
      formData.append("file", file);
    
      setLoading(true);
      setMessage("");
      setError("");
    
      try {
        const response = await fetch("http://localhost:8083/api/books/upload", {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`, // Pass token in headers
          },
          body: formData,
        });
    
        const result = await response.json();
        if (response.ok) {
          setMessage(result.message || "File uploaded successfully!");
          setFile(null); // Reset file input after successful upload
    
          // SweetAlert2 success alert and close the modal after clicking OK
          Swal.fire({
            icon: 'success',
            title: 'Success!',
            text: result.message || 'File uploaded successfully!',
            confirmButtonText: 'OK',
          }).then(() => {
            toggleModal2(); // Close the modal after success
          });
    
        } else {
          setError(result.message || "Error uploading file.");
    
          // SweetAlert2 error alert and close the modal after clicking OK
          Swal.fire({
            icon: 'error',
            title: 'Error!',
            text: result.message || 'Error uploading file.',
            confirmButtonText: 'OK',
          }).then(() => {
            toggleModal2(); // Close the modal after error
          });
        }
      } catch (error) {
        setError("Error uploading file: " + error.message);
    
        // SweetAlert2 error alert and close the modal after clicking OK
        Swal.fire({
          icon: 'error',
          title: 'Error!',
          text: "Error uploading file: " + error.message,
          confirmButtonText: 'OK',
        }).then(() => {
          toggleModal2(); // Close the modal after exception
        });
      } finally {
        setLoading(false);
      }
    };
    
  const handleInputChange = (e) => {
    const { name, value } = e.target;

    // Update form data as usual
    setFormData({
      ...formData,
      [name]: value
    });
  };




  const handleUpdate = async () => {
    try {
      const token = localStorage.getItem("authToken");
      // const userId = getUserIdFromToken(token);

      const updatedFormData = {
        ...formData
      };

      const response = await fetch(`http://localhost:8083/api/books/${selectedBook.bookId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedFormData),
      });

      if (!response.ok) {
        throw new Error("Failed to update book");
      }

      const updatedBook = await response.json();
      setBooks(books.map((book) => (book.bookId === updatedBook.bookId ? updatedBook : book)));
      toggleModal(); // Close the modal
      Swal.fire({
        icon: "success",
        title: "Success!",
        text: "Book updated successfully!",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error!",
        text: error.message || "An unexpected error occurred.",
      });
    }
  };

  const handleDelete = async (bookId) => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No token found");
      }

      const response = await fetch(`http://localhost:8083/api/books/${bookId}/user/${getUserIdFromToken(token)}`, {
        method: 'DELETE',
        headers: {
          "Authorization": `Bearer ${token}` // Pass the token in the Authorization header
        }
      });

      if (!response.ok) {
        throw new Error('Failed to delete book');
      }

      setBooks(books.filter((book) => book.bookId !== bookId)); // Remove the deleted book
    } catch (error) {
      setError(error.message);
    }
  };

  const handleSubmit = async () => {
    try {
      // Check for missing authentication token
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No authentication token found.");
      }

      const userId = getUserIdFromToken(token);
      if (!userId) {
        throw new Error("Invalid user ID in token.");
      }

      // Validate price and quantity
      if (formData.price === '' || formData.price < 0) {
        throw new Error("Price must be a non-negative number.");
      }

      if (formData.quantity === '' || formData.quantity < 0) {
        throw new Error("Quantity must be a non-negative number.");
      }

      // Create the new form data, linking category and author IDs
      const newFormData = {
        ...formData,
        userId,
        category: { categoryId: formData.categoryId }, // Link category
        author: { authorId: formData.authorId },       // Link author
      };

      // Make the API request to create a new book
      const response = await fetch(`http://localhost:8083/api/books/user/${userId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(newFormData),
      });

      // Check if the response is OK
      if (!response.ok) {
        throw new Error("Failed to create book. Invalid ISBN or title.");
      }

      // Parse the response JSON and add the new book to the state
      const newBook = await response.json();
      setBooks((prevBooks) => [...prevBooks, newBook]); // Add the new book to the list of books

      // Close the modal (if you have one)
      toggleModal();

      // Display success message
      Swal.fire({
        icon: "success",
        title: "Success!",
        text: "Book added successfully!",
      });
    } catch (error) {
      // Display error message with SweetAlert2
      Swal.fire({
        icon: "error",
        title: "Error!",
        text: error.message || "An unexpected error occurred.",
      });
    }
  };






  // const handleSubmit = async () => {
  //   try {
  //     const token = localStorage.getItem("authToken");
  //     const userId = getUserIdFromToken(token);

  //     const newFormData = {
  //       ...formData,
  //       userId,
  //       category: { categoryId: formData.categoryId }, // Link category
  //       author: { authorId: formData.authorId },       // Link author
  //     };

  //     const response = await fetch(`http://localhost:8083/api/books/user/${userId}`, {
  //       method: "POST",
  //       headers: {
  //         "Content-Type": "application/json",
  //         "Authorization": `Bearer ${token}`,
  //       },
  //       body: JSON.stringify(newFormData),
  //     });

  //     if (!response.ok) {
  //       throw new Error("Failed to create book invalid isbn or title");
  //     }

  //     const newBook = await response.json();
  //     setBooks([...books, newBook]); // Add the new book to the state
  //     toggleModal(); // Close the modal
  //     Swal.fire({
  //       icon: "success",
  //       title: "Success!",
  //       text: "Book added successfully!",
  //     });
  //   } catch (error) {
  //     Swal.fire({
  //       icon: "error",
  //       title: "Error!",
  //       text: error.message || "An unexpected error occurred.",
  //     });
  //   }
  // };




  console.log(categories)


  return (
    <>
      <Header />
      <Container className="mt--7" fluid>
        <Button className="btn-icon btn-3 mb-3" color="default" type="button" onClick={() => toggleModal()}>
          <span className="btn-inner--text">Add</span>
          <span className="btn-inner--icon">
            <i className="ni ni-fat-add" />
          </span>
        </Button>
        <Button className="btn-icon btn-3 mb-3" color="default" onClick={() => toggleModal2()}>
          <span className="btn-inner--text">Upload CSV</span>
          <span className="btn-inner--icon">
            <i className="ni ni-fat-add" />
          </span>
        </Button>

        <Row>
          <div className="col">
            <Card className="shadow">
              <CardHeader className="border-0">
                <h3 className="mb-0">Books Table</h3>
              </CardHeader>
              <InputGroup className="input-group-alternative">
                <InputGroupAddon addonType="prepend">
                  <InputGroupText>
                    <i className="fas fa-search" />
                  </InputGroupText>
                </InputGroupAddon>
                <Input
                  placeholder="Search Books "
                  type="text"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </InputGroup>
              {/* <Table className="align-items-center table-flush" responsive>
            <thead className="thead-light">
              <tr>
                <th scope="col">Book Id</th>
                <th scope="col">Book Title</th>
                <th scope="col">ISBN</th>
                <th scope="col">Description</th>
                <th scope="col">Author Name</th>
                <th scope="col">Category Name</th>
                <th scope="col">Quantity</th>
                <th scope="col">Price</th>
                <th scope="col">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredBooks.length > 0 ? (
                filteredBooks.map((book) => (
                  <tr key={book.bookId}>
                    <th scope="row">{book.bookId}</th>
                    <td>{book.title}</td>
                    <td>{book.isbn}</td>
                    <td>{book.description || "N/A"}</td>
                    <td>{book.authorName || book.author.authorName}</td>
                    <td>{book.categoryName || book.category.name}</td>
                    <td>{book.quantity}</td>
                    <td>${book.price.toFixed(2)}</td>
                    <td>
                      <Button
                        className="btn-icon btn-3"
                        color="success"
                        type="button"
                        onClick={() => toggleModal(book)}
                      >
                        <span className="btn-inner--icon">
                          <i className="ni ni-settings" />
                        </span>
                        <span className="btn-inner--text">Edit</span>
                      </Button>
                      <Button
                        className="btn-icon btn-3"
                        color="danger"
                        type="button"
                        onClick={() => handleDelete(book.bookId)}
                      >
                        <span className="btn-inner--icon">
                          <i className="ni ni-bag-17" />
                        </span>
                        <span className="btn-inner--text">Delete</span>
                      </Button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="9" className="text-center">
                    No books found.
                  </td>
                </tr>
              )}
            </tbody>
          </Table> */}
              <Table className="align-items-center table-flush" responsive>
                <thead className="thead-light">
                  <tr>
                    <th scope="col">Book Id</th>
                    <th scope="col">Book Title</th>
                    <th scope="col">ISBN</th>
                    <th scope="col">Description</th>
                    <th scope="col">Author Name</th>
                    <th scope="col">Category Name</th>
                    <th scope="col">Quantity</th>
                    <th scope="col">Price</th>
                    <th scope="col">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredBooks.length > 0 ? (
                    filteredBooks.map((book) => (
                      <tr key={book.bookId}>
                        <th scope="row">{book.bookId}</th>
                        <td>{book.title}</td>
                        <td>{book.isbn}</td>
                        <td>{book.description || "N/A"}</td>
                        <td>{book.authorName || book.author?.authorName}</td>
                        <td>{book.categoryName || book.category?.name}</td>
                        <td>{book.quantity}</td>
                        <td>${book.price.toFixed(2)}</td>
                        <td>
                          <Button
                            className="btn-icon btn-3"
                            color="success"
                            type="button"
                            onClick={() => toggleModal(book)}  // Assuming toggleModal is defined elsewhere
                          >
                            <span className="btn-inner--icon">
                              <i className="ni ni-settings" />
                            </span>
                            <span className="btn-inner--text">Edit</span>
                          </Button>
                          <Button
                            className="btn-icon btn-3"
                            color="danger"
                            type="button"
                            onClick={() => handleDelete(book.bookId)}  // Assuming handleDelete is defined elsewhere
                          >
                            <span className="btn-inner--icon">
                              <i className="ni ni-bag-17" />
                            </span>
                            <span className="btn-inner--text">Delete</span>
                          </Button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="9" className="text-center">
                        No books found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </Table>
              <CardFooter className="py-4">
                <nav aria-label="...">
                  <Pagination
                    className="pagination justify-content-end mb-0"
                    listClassName="justify-content-end mb-0"
                  >
                    <PaginationItem className="disabled">
                      <PaginationLink
                        href="#pablo"
                        onClick={(e) => e.preventDefault()}
                        tabIndex="-1"
                      >
                        <i className="fas fa-angle-left" />
                        <span className="sr-only">Previous</span>
                      </PaginationLink>
                    </PaginationItem>
                    <PaginationItem className="active">
                      <PaginationLink
                        href="#pablo"
                        onClick={(e) => e.preventDefault()}
                      >
                        1
                      </PaginationLink>
                    </PaginationItem>
                    <PaginationItem>
                      <PaginationLink
                        href="#pablo"
                        onClick={(e) => e.preventDefault()}
                      >
                        2
                      </PaginationLink>
                    </PaginationItem>
                    <PaginationItem>
                      <PaginationLink
                        href="#pablo"
                        onClick={(e) => e.preventDefault()}
                      >
                        3
                      </PaginationLink>
                    </PaginationItem>
                    <PaginationItem>
                      <PaginationLink
                        href="#pablo"
                        onClick={(e) => e.preventDefault()}
                      >
                        <i className="fas fa-angle-right" />
                        <span className="sr-only">Next</span>
                      </PaginationLink>
                    </PaginationItem>
                  </Pagination>
                </nav>
              </CardFooter>
            </Card>
          </div>
        </Row>

        {/* <Row>
            <div className="col">
              <Card className="shadow">
                <CardHeader className="border-0">
                  <h3 className="mb-0">Books table</h3>
                </CardHeader>
                <InputGroup className="input-group-alternative">
                <InputGroupAddon addonType="prepend">
                  <InputGroupText>
                    <i className="fas fa-search" />
                  </InputGroupText>
                </InputGroupAddon>
                <Input placeholder="Search" type="text" />
              </InputGroup>
                <Table className="align-items-center table-flush" responsive>
  <thead className="thead-light">
    <tr>
      <th scope="col">Book Id</th>
      <th scope="col">Book Title</th>
      <th scope="col">ISBN</th>
      <th scope="col">Description</th>
      <th scope="col">Author Name</th>
      <th scope="col">Category Name</th>
      <th scope="col">Quantity</th>
      <th scope="col">Price</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
    {books.length > 0 ? (
      books.map((book) => (
        <tr key={book.bookId}>
          <th scope="row">{book.bookId}</th>
          <td>{book.title}</td>
          <td>{book.isbn}</td>
          <td>{book.description || "N/A"}</td>
          <td>{book.authorName || book.author.authorName}</td>
          <td>{book.categoryName || book.category.name}</td>

          <td>{book.quantity}</td>
          <td>${book.price.toFixed(2)}</td>
          <td>
            <Button
              className="btn-icon btn-3"
              color="success"
              type="button"
              onClick={() => toggleModal(book)}
            >
              <span className="btn-inner--icon">
                <i className="ni ni-settings" />
              </span>
              <span className="btn-inner--text">Edit</span>
            </Button>
            <Button
              className="btn-icon btn-3"
              color="danger"
              type="button"
              onClick={() => handleDelete(book.bookId)}
            >
              <span className="btn-inner--icon">
                <i className="ni ni-bag-17" />
              </span>
              <span className="btn-inner--text">Delete</span>
            </Button>
          </td>
        </tr>
      ))
    ) : (
      <tr>
        <td colSpan="9" className="text-center">
          No books available.
        </td>
      </tr>
    )}
  </tbody>
</Table>

                <CardFooter className="py-4">
                  <nav aria-label="...">
                    <Pagination className="pagination justify-content-end mb-0" listClassName="justify-content-end mb-0">
                      <PaginationItem className="disabled">
                        <PaginationLink href="#pablo" onClick={(e) => e.preventDefault()} tabIndex="-1">
                          <i className="fas fa-angle-left" />
                          <span className="sr-only">Previous</span>
                        </PaginationLink>
                      </PaginationItem>
                      <PaginationItem className="active">
                        <PaginationLink href="#pablo" onClick={(e) => e.preventDefault()}>1</PaginationLink>
                      </PaginationItem>
                      <PaginationItem>
                        <PaginationLink href="#pablo" onClick={(e) => e.preventDefault()}>2</PaginationLink>
                      </PaginationItem>
                      <PaginationItem>
                        <PaginationLink href="#pablo" onClick={(e) => e.preventDefault()}>3</PaginationLink>
                      </PaginationItem>
                      <PaginationItem>
                        <PaginationLink href="#pablo" onClick={(e) => e.preventDefault()}>
                          <i className="fas fa-angle-right" />
                          <span className="sr-only">Next</span>
                        </PaginationLink>
                      </PaginationItem>
                    </Pagination>
                  </nav>
                </CardFooter>
              </Card>
            </div>
          </Row> */}
      </Container>

      {/* Modal for adding or editing book */}
      <Modal className="modal-dialog-centered" isOpen={modalOpen} toggle={() => toggleModal()}>
        <div className="modal-header">
          <h5 className="modal-title" id="exampleModalLabel">
            {selectedBook ? 'Edit Book' : 'Add Book'}
          </h5>
          <button aria-label="Close" className="close" data-dismiss="modal" type="button" onClick={() => toggleModal()}>
            <span aria-hidden={true}>×</span>
          </button>
        </div>
        <div className="modal-body">
          <Form>
            <FormGroup>
              <Label for="title">Title</Label>
              <Input
                type="text"
                name="title"
                id="title"
                value={formData.title}
                onChange={handleInputChange}
              />
            </FormGroup>
            <FormGroup>
              <Label for="isbn">ISBN</Label>
              <Input
                type="text"
                name="isbn"
                id="isbn"
                value={formData.isbn}
                onChange={handleInputChange}
              />
            </FormGroup>
            <FormGroup>
              <Label for="price">Price</Label>
              <Input
                type="number"
                name="price"
                id="price"
                value={formData.price}
                onChange={handleInputChange}
              />
            </FormGroup>
            <FormGroup>
              <Label for="quantity">Quantity</Label>
              <Input
                type="number"
                name="quantity"
                id="quantity"
                value={formData.quantity}
                onChange={handleInputChange}
              />
            </FormGroup>
            <FormGroup>
              <Label for="description">Description</Label>
              <Input
                type="textarea"
                name="description"
                id="description"
                value={formData.description}
                onChange={handleInputChange}
              />
            </FormGroup>


            {/* <FormGroup>
  <Label for="author">Author</Label>
  <Input
    type="select"
    name="authorId"
    id="author"
    value={formData.authorId || ""}
    onChange={(e) => setFormData({ ...formData, authorId: e.target.value })}
  >
    <option value="" disabled>Select an author</option>
    {authors.map((author) => (
      <option key={author.authorId} value={author.authorId}>
        {author.authorName}
      </option>
    ))}
  </Input>
</FormGroup>

<FormGroup>
  <Label for="category">Category</Label>
  <Input
    type="select"
    name="categoryId"
    id="category"
    value={formData.categoryId || ""}
    onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
  >
    <option value="" disabled>Select a category</option>
    {categories.map((category) => (
      <option key={category.categoryId} value={category.categoryId}>
        {category.name}
      </option>
    ))}
  </Input>
</FormGroup> */}

            <FormGroup>
              <Label for="author">Author</Label>
              <Input
                type="select"
                name="authorId"
                id="author"
                value={formData.authorId || ""}
                onChange={(e) => setFormData({ ...formData, authorId: e.target.value })}
              >
                <option value="" disabled>Select an author</option>
                {authors.map((author) => (
                  <option key={author.authorId} value={author.authorId}>
                    {author.authorName}
                  </option>
                ))}
              </Input>
            </FormGroup>

            <FormGroup>
              <Label for="category">Category</Label>
              <Input
                type="select"
                name="categoryId"
                id="category"
                value={formData.categoryId || ""}
                onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
              >
                <option value="" disabled>Select a category</option>
                {categories.map((category) => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.name}
                  </option>
                ))}
              </Input>
            </FormGroup>



          </Form>
        </div>
        <div className="modal-footer">
          <Button color="secondary" data-dismiss="modal" type="button" onClick={() => toggleModal()}>
            Close
          </Button>
          <Button
            color="primary"
            type="button"
            onClick={selectedBook ? handleUpdate : handleSubmit}
          >
            {selectedBook ? 'Save Changes' : 'Add Book'}
          </Button>
        </div>
      </Modal>

      {/* <Modal
      className="modal-dialog-centered"
      isOpen={exampleModal2}
      toggle={toggleModal2}
    >
      <div className="modal-header">
        <h5 className="modal-title" id="exampleModalLabel">
          Upload File
        </h5>
        <button
          aria-label="Close"
          className="close"
          type="button"
          onClick={toggleModal2}
        >
          <span aria-hidden={true}>×</span>
        </button>
      </div>
      <div className="modal-body">
        <Form onSubmit={handleUpload}>
          <FormGroup>
            <Input 
              type="file" 
              onChange={handleFileChange} 
              required 
            />
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {message && <p style={{ color: 'green' }}>{message}</p>}
          </FormGroup>
          <Button color="primary" type="submit">
            Upload
          </Button>
        </Form>
      </div>
      <div className="modal-footer">
        <Button color="secondary" type="button" onClick={toggleModal2}>
          Close
        </Button>
      </div>
    </Modal> */}
    <Modal className="modal-dialog-centered" isOpen={exampleModal2} toggle={toggleModal2}>
      <div className="modal-header">
        <h5 className="modal-title" id="exampleModalLabel">
          Upload File
        </h5>
        <button aria-label="Close" className="close" type="button" onClick={toggleModal2}>
          <span aria-hidden={true}>×</span>
        </button>
      </div>
      <div className="modal-body">
        <Form onSubmit={handleUpload}>
          <FormGroup>
            <Input type="file" onChange={handleFileChange} required />
            {/* {error && <p style={{ color: "red" }}>{error}</p>}
            {message && <p style={{ color: "green" }}>{message}</p>} */}
          </FormGroup>
          <Button color="primary" type="submit" disabled={loading}>
            {loading ? <Spinner size="sm" /> : "Upload"}
          </Button>
        </Form>
      </div>
      <div className="modal-footer">
        <Button color="secondary" type="button" onClick={toggleModal2}>
          Close
        </Button>
      </div>
    </Modal>
    </>
  );
};

export default Books;
