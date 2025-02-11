import {
  Card,
  CardHeader,
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
  Pagination,
  PaginationItem,
  PaginationLink
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode";
import Swal from 'sweetalert2';
// import { Col } from "reactstrap";


const getUserIdFromToken = (token) => {
  try {
    const decoded = jwtDecode(token);
    return decoded.userId;
  } catch (error) {
    console.error("Invalid token", error);
    return null;
  }
};

const Author = () => {
  const [authors, setAuthors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedAuthor, setSelectedAuthor] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [file, setFile] = useState(null);
  const [message, setMessage] = useState('');

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 7;

  const filteredAuthors = authors.filter((author) => {
    const search = searchTerm.toLowerCase();
    return author.authorName?.toLowerCase().includes(search);
  });

  // Pagination logic
  const totalPages = Math.ceil(filteredAuthors.length / itemsPerPage);
  const paginatedAuthors = filteredAuthors.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (page) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const [formData, setFormData] = useState({
    authorName: '',
    biography: ''
  });

  const toggleModal = (author = null) => {
    setSelectedAuthor(author);
    setFormData({
      authorName: author ? author.authorName : '',
      biography: author ? author.biography : ''
    });
    setModalOpen(!modalOpen);
  };

  const [exampleModal2, setExampleModal2] = useState(false);

  // Renamed toggle function to toggleModal2
  const toggleModal2 = () => {
    setExampleModal2(!exampleModal2);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleUpdate = async () => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No token found");
      }
  
      const response = await fetch(`http://localhost:8083/api/authors/${selectedAuthor.authorId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });
  
      if (!response.ok) {
        throw new Error('Failed to update author');
      }
  
      const updatedAuthor = await response.json();
      setAuthors(authors.map((author) =>
        author.authorId === updatedAuthor.authorId ? updatedAuthor : author
      ));
      toggleModal();
    } catch (error) {
      setError(error.message);
    }
  };
  
  const handleDelete = async (authorId) => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No token found");
      }
  
      const response = await fetch(`http://localhost:8083/api/authors/${authorId}`, {
        method: 'DELETE',
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
  
      if (!response.ok) {
        throw new Error('Failed to delete author');
      }
  
      setAuthors(authors.filter((author) => author.authorId !== authorId));
    } catch (error) {
      setError(error.message);
    }
  };
  
  const handleSubmit = async () => {
    try {
      const token = localStorage.getItem("authToken");
      const userId = getUserIdFromToken(token);
  
      const newFormData = {
        ...formData,
        user: { userId }
      };
  
      const response = await fetch("http://localhost:8083/api/authors", {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(newFormData)
      });
  
      if (!response.ok) {
        let errorMessage = 'Author already exists';
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch (jsonError) {}
  
        throw new Error(errorMessage);
      }
  
      const newAuthor = await response.json();
      setAuthors([...authors, newAuthor]);
      toggleModal();
  
      Swal.fire({
        icon: 'success',
        title: 'Success!',
        text: 'Author added successfully!',
      });
    } catch (error) {
      Swal.fire({
        icon: 'error',
        title: 'Error!',
        text: error.message || 'An unexpected error occurred.',
      });
    }
  };

   // Handle file change
  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && selectedFile.type === 'text/csv') {
      setFile(selectedFile);
      setError(null);
    } else {
      setError('Please upload a valid CSV file');
    }
  };

  // Handle form submission
  const handleFileSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("authToken");

    if (!token) {
      setError('No token found');
      return;
    }

    if (!file) {
      setError('Please select a CSV file to upload');
      return;
    }

    const authorformData = new FormData();
    authorformData.append('file', file);

    try {
      // Send the CSV file using fetch
      const response = await fetch('http://localhost:8083/api/authors/upload-csv', {
        method: 'POST',
        headers: {
          "Authorization": `Bearer ${token}`,
        },
        body: authorformData,
      });

      if (!response.ok) {
        throw new Error('Error uploading file');
      }

      setMessage('File uploaded successfully');
      setError(null);

      // Close the modal and refresh the page after successful upload
      toggleModal2();
      window.location.reload();  // Refresh the page

    } catch (err) {
      setMessage('');
      setError('Error uploading file');
    }
  };


//  useEffect(() => {
//   const fetchAuthors = async () => {
//     try {
//       const token = localStorage.getItem("authToken");
//       if (!token) {
//         throw new Error("No token found");
//       }

//       const userId = getUserIdFromToken(token);
//       if (!userId) {
//         throw new Error("Invalid user ID from token");
//       }

//       // Adjust the API URL if needed
//       const response = await fetch(`http://localhost:8083/api/authors?userId=${userId}`, {
//         method: 'GET',
//         headers: {
//           "Authorization": `Bearer ${token}`,
//           "Content-Type": "application/json",
//         }
//       });

//       if (!response.ok) {
//         throw new Error("Failed to fetch authors");
//       }

//       const data = await response.json();
//       setAuthors(Array.isArray(data) ? data : []);
//     } catch (error) {
//       setError(error.message);
//     } finally {
//       setLoading(false);
//     }
//   };

//   fetchAuthors();
// }, []);

useEffect(() => {
  const fetchAuthors = async () => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No token found");
      }

      const response = await fetch(`http://localhost:8083/api/authors/my-authors`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) {
        throw new Error("Failed to fetch authors");
      }

      const data = await response.json();
      setAuthors(Array.isArray(data) ? data : []);
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  fetchAuthors();
}, []);


  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <>
      <Header />
      <Container className="mt--7" fluid>
        <Button className="btn-icon btn-3 mb-3" color="default" onClick={() => toggleModal()}>
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
                <h3 className="mb-0">Authors Table</h3>
              </CardHeader>
             
              <InputGroup className="input-group-alternative">
                <InputGroupAddon addonType="prepend">
                  <InputGroupText>
                    <i className="fas fa-search" />
                  </InputGroupText>
                </InputGroupAddon>
                <Input
                  placeholder="Search Author"
                  type="text"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </InputGroup>
              <Table className="align-items-center table-flush" responsive>
                <thead className="thead-light">
                  <tr>
                    <th scope="col">Author Id</th>
                    <th scope="col">Author Name</th>
                    <th scope="col">Biography</th>
                    <th scope="col">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedAuthors.map((author) => (
                    <tr key={author.authorId}>
                      <th scope="row">{author.authorId}</th>
                      <td>{author.authorName}</td>
                      <td>{author.biography}</td>
                      <td>
                        <Button className="btn-icon btn-3" color="success" onClick={() => toggleModal(author)}>
                          <span className="btn-inner--icon">
                            <i className="ni ni-settings" />
                          </span>
                          <span className="btn-inner--text">Edit</span>
                        </Button>
                        <Button className="btn-icon btn-3" color="danger" onClick={() => handleDelete(author.authorId)}>
                          <span className="btn-inner--icon">
                            <i className="ni ni-bag-17" />
                          </span>
                          <span className="btn-inner--text">Delete</span>
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>

              {/* Pagination */}
              <nav>
                <Pagination className="pagination justify-content-center mt-3">
                  <PaginationItem disabled={currentPage === 1}>
                    <PaginationLink previous onClick={() => handlePageChange(currentPage - 1)} />
                  </PaginationItem>
                  {[...Array(totalPages)].map((_, i) => (
                    <PaginationItem active={i + 1 === currentPage} key={i}>
                      <PaginationLink onClick={() => handlePageChange(i + 1)}>
                        {i + 1}
                      </PaginationLink>
                    </PaginationItem>
                  ))}
                  <PaginationItem disabled={currentPage === totalPages}>
                    <PaginationLink next onClick={() => handlePageChange(currentPage + 1)} />
                  </PaginationItem>
                </Pagination>
              </nav>
            </Card>
          </div>
        </Row>
      </Container>

      {/* Modal for adding or editing author */}
      <Modal className="modal-dialog-centered" isOpen={modalOpen} toggle={() => toggleModal()}>
        <div className="modal-header">
          <h5 className="modal-title">{selectedAuthor ? 'Edit Author' : 'Add Author'}</h5>
          <button className="close" data-dismiss="modal" onClick={() => toggleModal()}>
            <span aria-hidden={true}>×</span>
          </button>
        </div>
        <div className="modal-body">
          <Form>
            <FormGroup>
              <Label for="authorName">Author Name</Label>
              <Input type="text" name="authorName" id="authorName" value={formData.authorName.trim()} onChange={handleInputChange} />
            </FormGroup>
            <FormGroup>
              <Label for="biography">Biography</Label>
              <Input type="textarea" name="biography" id="biography" value={formData.biography} onChange={handleInputChange} />
            </FormGroup>
          </Form>
        </div>
        <div className="modal-footer">
          <Button color="secondary" onClick={() => toggleModal()}>Close</Button>
          <Button color="primary" onClick={selectedAuthor ? handleUpdate : handleSubmit}>
            {selectedAuthor ? 'Save Changes' : 'Add Author'}
          </Button>
        </div>
      </Modal>

      
      <Modal
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
        <Form onSubmit={handleFileSubmit}>
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
    </Modal>
      
    </>
  );
};

export default Author;
