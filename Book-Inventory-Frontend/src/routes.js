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
import Index from "views/Index.js";
import Profile from "views/examples/Profile.js";
// import Maps from "views/examples/Maps.js";
import Register from "views/examples/Register.js";
import Login from "views/examples/Login.js";
import Tables from "views/examples/Tables.js";
import Author from "views/examples/Author.js";
// import Icons from "views/examples/Icons.js";
import Books from "views/examples/Books.js"
// import ForgotPassword from "views/examples/ForgotPassword"; 
import ResetPassword from "views/examples/ResetPassword";
import ForgotPassword from "views/examples/ForgotPassword"; 
// import ResetPassword from "views/examples/ResetPassword";

var routes = [
  {
    path: "/index",
    name: "Dashboard",
    icon: "ni ni-tv-2 text-primary",
    component: <Index />,
    layout: "/admin",
  },
  // {
  //   path: "/icons",
  //   name: "Icons",
  //   icon: "ni ni-planet text-blue",
  //   component: <Icons />,
  //   layout: "/admin",
  // },
  // {
  //   path: "/maps",
  //   name: "Maps",
  //   icon: "ni ni-pin-3 text-orange",
  //   component: <Maps />,
  //   layout: "/admin",
  // },
  {
    path: "/user-profile",
    name: "User Profile",
    icon: "ni ni-single-02 text-yellow",
    component: <Profile />,
    layout: "/admin",
  },
  {
    path: "/tables",
    name: "Categories",
    icon: "ni ni-books text-red",
    component: <Tables />,
    layout: "/admin",
  },
  {
    path: "/authors",
    name: "Authors", 
    icon: "ni ni-bullet-list-67 text-blue",
    component: <Author />,
    layout: "/admin",
  },
  {
    path: "/books",
    name: "Books", 
    icon: "ni ni ni-book-bookmark text-orange",
    component: <Books />,
    layout: "/admin",
  },
  {
    path: "/login",
    // name: "Login",
    // icon: "ni ni-key-25 text-info",
    component: <Login />,
    layout: "/auth",
  },
  {
    path: "/register",
    // name: "Register",
    // icon: "ni ni-circle-08 text-pink",
    component: <Register />,
    layout: "/auth",
  },
  {
    path: "/forgot-password",
    component: <ForgotPassword />,
    layout: "/auth",
  },
  {
    path: "/reset-password",
    component: <ResetPassword />,
    layout: "/auth",
  }
];
export default routes;
