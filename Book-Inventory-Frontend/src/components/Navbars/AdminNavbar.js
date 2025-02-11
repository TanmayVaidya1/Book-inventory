import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import {
  DropdownMenu,
  DropdownItem,
  UncontrolledDropdown,
  DropdownToggle,
  Form,
  FormGroup,
  InputGroupAddon,
  InputGroupText,
  Input,
  InputGroup,
  Navbar,
  Nav,
  Container,
  Media,
} from "reactstrap";

const AdminNavbar = (props) => {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [profileImage, setProfileImage] = useState(null);

  // Function to decode token and extract user details
  const getUserDetailsFromToken = (token) => {
    try {
      const decoded = jwtDecode(token);
      return { 
        username: decoded.name || "User", 
        userId: decoded.userId // Ensure `userId` exists in your token
      };
    } catch (error) {
      console.error("Invalid token", error);
      return { username: "User", userId: null };
    }
  };

  // Fetch Username from API
  const getUserName = async () => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) throw new Error("No token found");

      const response = await fetch(`http://localhost:8083/auth/username`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": `Bearer ${token}`
        }
      });

      if (!response.ok) throw new Error("Failed to fetch username");

      const userNameRes = await response.text();
      setUsername(userNameRes);
    } catch (error) {
      console.error(error.message);
    }
  };

  // Fetch User Profile Image
  const getUserProfileImage = async (userId) => {
    try {
      if (!userId) return;

      const token = localStorage.getItem("authToken");
      if (!token) throw new Error("No token found");

      const imageResponse = await fetch(`http://localhost:8083/get-profile-image/${userId}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          "Authorization": `Bearer ${token}`
        }
      });

      if (!imageResponse.ok) throw new Error("Failed to fetch profile image");

      const imageBlob = await imageResponse.blob();
      setProfileImage(URL.createObjectURL(imageBlob));
    } catch (error) {
      console.error(error.message);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("authToken");

    if (!token) {
      navigate("/auth/login");
      return;
    }

    const { username, userId } = getUserDetailsFromToken(token);
    setUsername(username);

    // Fetch additional data
    getUserName();
    getUserProfileImage(userId);
  }, [navigate]);

  // Logout handler
  const handleLogout = () => {
    localStorage.removeItem("authToken");
    navigate("/auth/login");
  };

  return (
    <Navbar className="navbar-top navbar-dark" expand="md" id="navbar-main">
      <Container fluid>
        <Link className="h4 mb-0 text-white text-uppercase d-none d-lg-inline-block" to="/">
          {props.brandText}
        </Link>
        <Form className="navbar-search navbar-search-dark form-inline mr-3 d-none d-md-flex ml-lg-auto">
          <FormGroup className="mb-0">
            <InputGroup className="input-group-alternative">
              <InputGroupAddon addonType="prepend">
                <InputGroupText>
                  <i className="fas fa-search" />
                </InputGroupText>
              </InputGroupAddon>
              <Input placeholder="Search" type="text" />
            </InputGroup>
          </FormGroup>
        </Form>
        <Nav className="align-items-center d-none d-md-flex" navbar>
          <UncontrolledDropdown nav>
            <DropdownToggle className="pr-0" nav>
              <Media className="align-items-center">
                <span className="avatar avatar-sm rounded-circle">
                  <img
                    alt="Profile"
                    src={profileImage || require("../../assets/img/theme/team-4-800x800.jpg")}
                  />
                </span>
                <Media className="ml-2 d-none d-lg-block">
                  <span className="mb-0 text-sm font-weight-bold">
                    {username}
                  </span>
                </Media>
              </Media>
            </DropdownToggle>
            <DropdownMenu className="dropdown-menu-arrow" right>
              <DropdownItem className="noti-title" header tag="div">
                <h6 className="text-overflow m-0">Welcome!</h6>
              </DropdownItem>
              <DropdownItem to="/admin/user-profile" tag={Link}>
                <i className="ni ni-single-02" />
                <span>My profile</span>
              </DropdownItem>
              <DropdownItem to="/admin/user-profile" tag={Link}>
                <i className="ni ni-settings-gear-65" />
                <span>Settings</span>
              </DropdownItem>
              <DropdownItem to="/admin/user-profile" tag={Link}>
                <i className="ni ni-calendar-grid-58" />
                <span>Activity</span>
              </DropdownItem>
              <DropdownItem to="/admin/user-profile" tag={Link}>
                <i className="ni ni-support-16" />
                <span>Support</span>
              </DropdownItem>
              <DropdownItem divider />
              <DropdownItem onClick={handleLogout}>
                <i className="ni ni-user-run" />
                <span>Logout</span>
              </DropdownItem>
            </DropdownMenu>
          </UncontrolledDropdown>
        </Nav>
      </Container>
    </Navbar>
  );
};

export default AdminNavbar;
