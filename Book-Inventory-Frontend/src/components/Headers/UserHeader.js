/*!

=========================================================
* Argon Dashboard React - v1.2.4
=========================================================

* Product Page: https://www.creative-tim.com/product/argon-dashboard-react
* Copyright 2024 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://github.com/creativetimofficial/argon-dashboard-react/blob/master/LICENSE.md)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/

// reactstrap components
import {Container, Row, Col } from "reactstrap";
import React, { useState, useEffect } from 'react';


const UserHeader = () => {
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
  return (
    <>
      <div
        className="header pb-8 pt-5 pt-lg-8 d-flex align-items-center"
        style={{
          minHeight: "600px",
          backgroundImage:
            "url(" + require("../../assets/img/theme/profile-cover.jpg") + ")",
          backgroundSize: "cover",
          backgroundPosition: "center top",
        }}
      >
        {/* Mask */}
        <span className="mask bg-gradient-default opacity-8" />
        {/* Header container */}
        <Container className="d-flex align-items-center" fluid>
          <Row>
            <Col lg="7" md="10">
              <h1 className="display-2 text-white">Hello {updatedUser.name}</h1>
              <p className="text-white mt-0 mb-5">
                This is your profile page. You can see the progress you've made
                with your work and manage your projects or assigned tasks
              </p>
              {/* <Button
                color="info"
                href="#pablo"
                onClick={(e) => e.preventDefault()}
              >
                Edit profile
              </Button> */}
            </Col>
          </Row>
        </Container>
      </div>
    </>
  );
};

export default UserHeader;
