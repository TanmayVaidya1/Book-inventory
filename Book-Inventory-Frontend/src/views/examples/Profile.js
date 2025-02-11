import {
  Button,
  Card,
  CardHeader,
  CardBody,
  FormGroup,
  Form,
  Input,
  Container,
  Row,
  Col,
} from "reactstrap";
import UserHeader from "components/Headers/UserHeader.js";
import React, { useState, useEffect } from 'react';

const Profile = () => {
  const [userData, setUserData] = useState(null);
  const [updatedUser, setUpdatedUser] = useState({
    name: '',
    email: '',
    city: '',
    password: '',
    role:''
  });
  const [profileImage, setProfileImage] = useState(null);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState('');
  const token = localStorage.getItem('authToken'); 

  useEffect(() => {
    if (!token) {
      setError("No authentication token found");
      return;
    }

    fetch('http://localhost:8083/getUserInfo', {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` },
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.statusCode === 200) {
          setUserData(data.user);
          setUpdatedUser({
            name: data.user.name,
            email: data.user.email,
            city: data.user.city,
            password: '',
            role: data.user.role
          });

          fetch(`http://localhost:8083/get-profile-image/${data.user.userId}`)
            .then(res => res.blob())
            .then(blob => {
              if (blob.size > 0) {
                setProfileImage(URL.createObjectURL(blob));
              }
            });

        } else {
          setError(data.message);
        }
      })
      .catch((error) => {
        setError('Error fetching user data: ' + error.message);
      });
  }, [token]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUpdatedUser(prev => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e) => {
    setProfileImage(e.target.files[0]);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!userData) return;

    const formData = new FormData();
    
    // Append updatedUser as a JSON string
    formData.append('updatedUser', JSON.stringify({
        name: updatedUser.name,
        email: updatedUser.email,
        city: updatedUser.city,
        role: updatedUser.role,
        password: updatedUser.password // optional, depending on the update
    }));

    // Append the profileImage file if it's provided
    if (profileImage instanceof File) {
      formData.append('profileImage', profileImage);
    }

    fetch(`http://localhost:8083/update/${userData.userId}`, {
      method: 'PUT',
      headers: { 
        'Authorization': `Bearer ${token}`,
        // No need to set 'Content-Type', since FormData automatically handles it
      },
      body: formData
    })
      .then(response => response.json())
      .then(data => {
        if (data.statusCode === 200) {
          setSuccessMessage('Profile updated successfully!');
          fetch(`http://localhost:8083/get-profile-image/${data.user.userId}`)
            .then(res => res.blob())
            .then(blob => {
              if (blob.size > 0) {
                setProfileImage(URL.createObjectURL(blob));
              }
            });
        } else {
          setError(data.message);
        }
      })
      .catch(error => setError('Error updating profile: ' + error.message));
};


  if (error) return <div>{error}</div>;
  if (!userData) return <div>Loading...</div>;

  return (
    <>
      <UserHeader />
      <Container className="mt--7" fluid>
        <Row>
          <Col className="order-xl-2 mb-5 mb-xl-0" xl="4">
            <Card className="card-profile shadow">
              <Row className="justify-content-center">
                <Col className="order-lg-2" lg="3">
                  <div className="card-profile-image">
                    <a href="#pablo" onClick={(e) => e.preventDefault()}>
                      <img
                        alt="image selected"
                        className="rounded-circle"
                        src={profileImage || require("../../assets/img/theme/team-4-800x800.jpg")}
                      />
                    </a>
                  </div>
                </Col>
              </Row>
              <CardHeader className="text-center border-0 pt-8 pt-md-4 pb-0 pb-md-4">
                <div className="d-flex justify-content-between">
                  <Button className="mr-4" color="info" size="sm">Connect</Button>
                  <Button className="float-right" color="default" size="sm">Message</Button>
                </div>
              </CardHeader>
              <CardBody className="pt-0 pt-md-4">
                <Row>
                  <div className="col">
                    <div className="card-profile-stats d-flex justify-content-center mt-md-5">
                      <div><span className="heading">22</span><span className="description">Friends</span></div>
                      <div><span className="heading">10</span><span className="description">Photos</span></div>
                      <div><span className="heading">89</span><span className="description">Comments</span></div>
                    </div>
                  </div>
                </Row>
                <div className="text-center">
                  <h3>{updatedUser.name}</h3>
                  <div className="h5 font-weight-300"><i className="ni location_pin mr-2" />{updatedUser.city}</div>
                  <hr className="my-4" />
                  <p>Update your profile details below.</p>
                </div>
              </CardBody>
            </Card>
          </Col>
          <Col className="order-xl-1" xl="8">
            <Card className="bg-secondary shadow">
              <CardHeader className="bg-white border-0">
                <Row className="align-items-center">
                  <Col xs="8"><h3 className="mb-0">My account</h3></Col>
                </Row>
              </CardHeader>
              <CardBody>
                <Form onSubmit={handleSubmit}>
                  <h6 className="heading-small text-muted mb-4">User information</h6>
                  <div className="pl-lg-4">
                    <Row>
                      <Col lg="6">
                        <FormGroup>
                          <label className="form-control-label">Name</label>
                          <Input type="text" name="name" value={updatedUser.name} onChange={handleInputChange} required />
                        </FormGroup>
                      </Col>
                      <Col lg="6">
                        <FormGroup>
                          <label className="form-control-label">Email address</label>
                          <Input type="email" name="email" value={updatedUser.email} onChange={handleInputChange} required />
                        </FormGroup>
                      </Col>
                    </Row>
                    <Row>
                      <Col lg="6">
                        <FormGroup>
                          <label className="form-control-label">City</label>
                          <Input type="text" name="city" value={updatedUser.city} onChange={handleInputChange} required />
                        </FormGroup>
                      </Col>
                      <Col lg="6">
                        <FormGroup>
                          <label className="form-control-label">Password</label>
                          <Input type="password" name="password" value={updatedUser.password} onChange={handleInputChange} />
                        </FormGroup>
                      </Col>
                    </Row>
                    <Row>
                      <Col lg="6">
                        <FormGroup>
                          <label className="form-control-label">Profile Image</label>
                          <Input type="file" accept="image/*" onChange={handleImageChange} />
                        </FormGroup>
                      </Col>
                    </Row>
                    <Row>
                      <Col lg="6">
                        <Button type="submit" color="primary">Update Profile</Button>
                      </Col>
                    </Row>
                    {successMessage && <p className="text-success mt-3">{successMessage}</p>}
                  </div>
                </Form>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </Container>
    </>
  );
};

export default Profile;
