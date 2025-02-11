import { useState } from "react";
import { Link } from "react-router-dom";
import Swal from "sweetalert2"; // Import SweetAlert2

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');

  const handleForgetPassword = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8083/auth/forgot-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email }),
      });

      const data = await response.json();

      // SweetAlert2 for success or error based on API response
      if (data.statusCode === 200) {
        Swal.fire({
          icon: 'success',
          title: 'Success!',
          text: data.message,
        });
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: data.message,
        });
      }

    } catch (error) {
      // SweetAlert2 for error in case of network issues
      Swal.fire({
        icon: 'error',
        title: 'Oops...',
        text: 'Error sending OTP. Please try again.',
      });
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch('http://localhost:8083/auth/reset-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, otp, password: newPassword }),
      });

      const data = await response.json();

      // SweetAlert2 for success or error based on API response
      if (data.statusCode === 200) {
        Swal.fire({
          icon: 'success',
          title: 'Password Reset Successful',
          text: data.message,
        });
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Oops...',
          text: data.message,
        });
      }

    } catch (error) {
      // SweetAlert2 for error in case of network issues
      Swal.fire({
        icon: 'error',
        title: 'Oops...',
        text: 'Error resetting password. Please try again.',
      });
    }
  };

  return (
    <div>
      <div className="p-6 max-w-md mx-auto bg-white shadow-md rounded-xl min-h-[400px]">
        <h2 className="text-xl font-bold mt-6 mb-3">Forgot Password</h2>
        <form onSubmit={handleForgetPassword}>
          <input
            type="email"
            className="w-full p-2 border rounded mb-2"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <button className="w-full bg-blue-500 text-white p-2 rounded btn btn-primary ml-2">Send Reset Link</button>
        </form>

        <h2 className="text-xl font-bold mt-3">Reset Password</h2>
        <form onSubmit={handleResetPassword}>
          <div>
            <input
              className="w-full p-2 border rounded mb-2"
              placeholder="Enter your email"
              type="email"
              id="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div>
            <input
              className="w-full p-2 border rounded mb-2"
              placeholder="Enter your OTP"
              type="text"
              id="otp"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              required
            />
          </div>
          <div>
            <input
              className="w-full p-2 border rounded mb-2"
              placeholder="Enter New password"
              type="password"
              id="newPassword"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>
          <button className="w-full bg-blue-500 text-white p-2 rounded btn btn-primary ml-2 mt-3" type="submit">Reset Password</button>
        </form>

        <div className="mt-4 text-center">
          <Link to="/auth/login">
            <strong>Sign In</strong>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
