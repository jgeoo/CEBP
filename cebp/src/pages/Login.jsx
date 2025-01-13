import { Box, Typography, TextField, Button } from "@mui/material";
import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom"; // Import useNavigate
import LoginImage from "../assets/home-image.jpg"; // Optional: Use a different image for login

export default function Login() {
    // State for form inputs
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    // Use navigate to redirect after successful login or to sign-up page
    const navigate = useNavigate();

    // Handle form submission
    const handleLogin = async (e) => {
        e.preventDefault();

        // Clear any previous error message
        setErrorMessage("");

        try {
            // Sending a POST request with the username and password
            const response = await axios.post("http://localhost:8080/api/users/login", {
                username,
                password,
            });

            // Handle successful login (example)
            if (response.status === 200) {
                console.log("Login successful:", response.data);
                localStorage.setItem("id",JSON.stringify(response.data.id));
                // Redirect user to the Home page
                navigate("/home"); // Use navigate to route to Home
            }
        } catch (error) {
            // Handle login failure (invalid credentials or API error)
            setErrorMessage("Invalid credentials. Please try again.");
            console.error("Login error:", error);
        }
    };

    // Navigate to the sign-up page
    const handleSignUp = () => {
        navigate("/signup"); // Redirect to the Sign Up page
    };

    return (
        <Box
            sx={{
                padding: { xs: "50px 20px", sm: "100px 30px", md: "100px 150px" },
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                flexWrap: "wrap",
                minHeight: "100vh",
            }}
        >
            <Box
                sx={{
                    maxWidth: "500px",
                    textAlign: { xs: "center", sm: "left" },
                    mb: { xs: "20px", sm: "0" },
                }}
            >
                <Typography
                    variant="h2"
                    sx={{
                        color: "#333",
                        fontWeight: "bold",
                        mb: 2,
                    }}
                >
                    Login to Your Account
                </Typography>
                <Typography
                    variant="body1"
                    sx={{
                        color: "#555",
                        mb: 3,
                        lineHeight: 1.6,
                    }}
                >
                    Access your stock trading portfolio, manage your investments, and more.
                </Typography>

                {/* Login Form */}
                <Box
                    component="form"
                    sx={{
                        display: "flex",
                        flexDirection: "column",
                        gap: 2,
                    }}
                    onSubmit={handleLogin} // Bind form submission to the handleLogin function
                >
                    <TextField
                        label="Username"
                        variant="outlined"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        sx={{
                            width: "100%",
                        }}
                    />
                    <TextField
                        label="Password"
                        type="password"
                        variant="outlined"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        sx={{
                            width: "100%",
                        }}
                    />
                    <Button
                        type="submit"
                        variant="contained"
                        color="primary"
                        sx={{
                            marginTop: 2,
                            padding: "10px 20px",
                            backgroundColor: "#007bff",
                        }}
                    >
                        Login
                    </Button>

                    {/* Sign Up Button */}
                    <Button
                        variant="text"
                        color="primary"
                        onClick={handleSignUp}
                        sx={{
                            marginTop: 2,
                            textTransform: "none",
                        }}
                    >
                        Don't have an account? Sign up here
                    </Button>
                </Box>

                {errorMessage && (
                    <Typography
                        variant="body2"
                        color="error"
                        sx={{
                            mt: 2,
                            textAlign: "center",
                        }}
                    >
                        {errorMessage}
                    </Typography>
                )}
            </Box>

            <Box
                component="img"
                src={LoginImage} // Optionally use a different image for the login page
                alt="Login Illustration"
                sx={{
                    height: { xs: "300px", sm: "400px", md: "500px" },
                    borderRadius: "8px",
                    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)",
                }}
            />
        </Box>
    );
}
