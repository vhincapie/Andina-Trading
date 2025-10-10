import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function AdminRoute({ children, allowedRoles = ["ADMIN"] }) {
  const { loading, isAuthenticated, user } = useAuth();
  if (loading) return null;
  if (!isAuthenticated) return <Navigate to="/login" replace />;

  const role = String(user?.rol || "").toUpperCase();
  const ok = allowedRoles.map((r) => r.toUpperCase()).includes(role);
  if (!ok) return <Navigate to="/" replace />; 
  return children;
}
