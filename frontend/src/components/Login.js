import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isRegister, setIsRegister] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const endpoint = isRegister ? "/auth/register" : "/auth/login";
      const { data } = await api.post(endpoint, { username, password });
      localStorage.setItem("token", data.token);
      navigate("/tasks");
    } catch (err) {
      setError(err.response?.data || "Something went wrong");
    }
  };

  return (
    <div style={{ maxWidth: 360, margin: "80px auto", fontFamily: "sans-serif" }}>
      <h2>{isRegister ? "Create account" : "Sign in to TaskFlow"}</h2>
      <form onSubmit={submit}>
        <input placeholder="Username" value={username}
               onChange={(e) => setUsername(e.target.value)} style={{ width: "100%", marginBottom: 8 }} />
        <input placeholder="Password" type="password" value={password}
               onChange={(e) => setPassword(e.target.value)} style={{ width: "100%", marginBottom: 8 }} />
        <button type="submit" style={{ width: "100%" }}>{isRegister ? "Register" : "Login"}</button>
      </form>
      {error && <p style={{ color: "red" }}>{String(error)}</p>}
      <button onClick={() => setIsRegister(!isRegister)} style={{ marginTop: 8 }}>
        {isRegister ? "Have an account? Login" : "New here? Register"}
      </button>
    </div>
  );
}
