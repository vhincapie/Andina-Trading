import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ComisionistaRoute({ children }) {
  const { loading, isAuthenticated, user } = useAuth();
  if (loading) return null;
  if (!isAuthenticated) return <Navigate to="/login" replace />;

  const role = String(user?.rol || "").toUpperCase();
  const rolesArr = Array.isArray(user?.roles)
    ? user.roles.map((r) => String(r).toUpperCase())
    : [];

  const isComi = role === "COMISIONISTA" || rolesArr.includes("COMISIONISTA");

  if (!isComi) return <Navigate to="/" replace />;
  return children;
}
