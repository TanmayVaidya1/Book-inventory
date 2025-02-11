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
  InputGroupText
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { useState, useEffect } from "react";
import { jwtDecode } from "jwt-decode"; // Ensure this library is installed
import Swal from "sweetalert2";

const getUserIdFromToken = (token) => {
  try {
    const decoded = jwtDecode(token);
    return decoded.userId; // Assuming `userId` is in the JWT payload
  } catch (error) {
    console.error("Invalid token", error);
    return null;
  }
};

const Tables = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [formData, setFormData] = useState({ name: "" });

  const filteredCategories = categories.filter((category) =>
    category.name?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const toggleModal = (category = null) => {
    setSelectedCategory(category);
    setFormData({
      name: category ? category.name : "",
    });
    setModalOpen(!modalOpen);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const token = localStorage.getItem("authToken");
        if (!token) {
          throw new Error("No token found");
        }
  
        const response = await fetch("http://localhost:8083/api/categories/my-category", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
  
        if (!response.ok) {
          throw new Error("Failed to fetch category");
        }
  
        const data = await response.json();
        setCategories(Array.isArray(data) ? data : []);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };
  
    fetchCategories();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  


  const handleSubmit = async () => {
    if (!formData.name.trim()) {
      Swal.fire({
        icon: "warning",
        title: "Validation Error",
        text: "Category name is required!",
      });
      return;
    }

    try {
      const token = localStorage.getItem("authToken");
      const userId = getUserIdFromToken(token);

      if (!userId) {
        throw new Error("Invalid token");
      }

      const newFormData = {
        ...formData,
        user: { userId },
      };

      const response = await fetch("http://localhost:8083/api/categories", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newFormData),
      });

      if (!response.ok) {
        let errorMessage = "Category already exists";
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch (jsonError) {}
        throw new Error(errorMessage);
      }

      const newCategory = await response.json();
      setCategories([...categories, newCategory]);
      toggleModal();

      Swal.fire({
        icon: "success",
        title: "Success!",
        text: "Category added successfully!",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error!",
        text: error.message || "An unexpected error occurred.",
      });
    }
  };

  const handleUpdate = async () => {
    if (!formData.name.trim()) {
      Swal.fire({
        icon: "warning",
        title: "Validation Error",
        text: "Category name is required!",
      });
      return;
    }

    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No token found");
      }

      const response = await fetch(
        `http://localhost:8083/api/categories/my-category/${selectedCategory.categoryId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(formData),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to update category");
      }

      const updatedCategory = await response.json();
      setCategories(
        categories.map((category) =>
          category.categoryId === updatedCategory.categoryId
            ? updatedCategory
            : category
        )
      );
      toggleModal();

      Swal.fire({
        icon: "success",
        title: "Success!",
        text: "Category updated successfully!",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error!",
        text: error.message || "An unexpected error occurred.",
      });
    }
  };

  const handleDelete = async (categoryId) => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No token found");
      }

      const response = await fetch(
        `http://localhost:8083/api/categories/${categoryId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to delete category");
      }

      setCategories(categories.filter((category) => category.categoryId !== categoryId));

      Swal.fire({
        icon: "success",
        title: "Deleted!",
        text: "Category deleted successfully.",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error!",
        text: error.message || "An unexpected error occurred.",
      });
    }
  };

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
        <Button
          className="btn-icon btn-3 mb-3"
          color="default"
          type="button"
          onClick={() => toggleModal()}
        >
          <span className="btn-inner--text">Add</span>
          <span className="btn-inner--icon">
            <i className="ni ni-fat-add" />
          </span>
        </Button>
        <Row>
          <div className="col">
            <Card className="shadow">
              <CardHeader className="border-0">
                <h3 className="mb-0">Category Tables</h3>
              </CardHeader>
              <InputGroup className="input-group-alternative">
                <InputGroupAddon addonType="prepend">
                  <InputGroupText>
                    <i className="fas fa-search" />
                  </InputGroupText>
                </InputGroupAddon>
                <Input
                  placeholder="Search Category"
                  type="text"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </InputGroup>
              <Table className="align-items-center table-flush" responsive>
                <thead className="thead-light">
                  <tr>
                    <th scope="col">Category Id</th>
                    <th scope="col">Category Name</th>
                    <th scope="col">Added By</th>
                    <th scope="col">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredCategories.map((category) => (
                    <tr key={category.categoryId}>
                      <th scope="row">{category.categoryId}</th>
                      <td>{category.name}</td>
                      <td>{category.user.name}</td>
                      <td>
                        <Button
                          className="btn-icon btn-3"
                          color="success"
                          type="button"
                          onClick={() => toggleModal(category)}
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
                          onClick={() => handleDelete(category.categoryId)}
                        >
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
              <CardFooter className="py-4">
                <Pagination className="pagination justify-content-end mb-0">
                  <PaginationItem className="disabled">
                    <PaginationLink
                      href="#pablo"
                      onClick={(e) => e.preventDefault()}
                      tabIndex="-1"
                    >
                      <i className="fas fa-angle-left" />
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
                </Pagination>
              </CardFooter>
            </Card>
          </div>
        </Row>
        <Modal
          className="modal-dialog-centered"
          isOpen={modalOpen}
          toggle={() => toggleModal()}
        >
          <div className="modal-header">
            <h5 className="modal-title" id="exampleModalLabel">
              {selectedCategory ? "Edit Category" : "Add Category"}
            </h5>
            <button
              aria-label="Close"
              className="close"
              type="button"
              onClick={() => toggleModal()}
            >
              <span aria-hidden={true}>×</span>
            </button>
          </div>
          <div className="modal-body">
            <Form>
              <FormGroup>
                <Label for="name">Category Name</Label>
                <Input
                  type="text"
                  name="name"
                  id="name"
                  value={formData.name}
                  onChange={handleInputChange}
                />
              </FormGroup>
            </Form>
          </div>
          <div className="modal-footer">
            <Button color="secondary" type="button" onClick={() => toggleModal()}>
              Close
            </Button>
            <Button
              color="primary"
              type="button"
              onClick={selectedCategory ? handleUpdate : handleSubmit}
            >
              {selectedCategory ? "Save Changes" : "Add Category"}
            </Button>
          </div>
        </Modal>
      </Container>
    </>
  );
};

export default Tables;
