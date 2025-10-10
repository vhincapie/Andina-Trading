import { Outlet } from "react-router-dom";
import ProtectedRoute from "../routes/ProtectedRoute";
import Navbar from "../components/Navbar";

export default function ProtectedLayout() {
  return (
    <ProtectedRoute>
      <>
        <Navbar />
        <div className="max-w-5xl mx-auto px-4 py-6">
          <Outlet />
        </div>
      </>
    </ProtectedRoute>
  );
}
