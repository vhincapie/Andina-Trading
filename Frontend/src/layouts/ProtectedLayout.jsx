import { Outlet } from "react-router-dom";
import ProtectedRoute from "../routes/ProtectedRoute";
import Navbar from "../components/Navbar";

export default function ProtectedLayout() {
  return (
    <ProtectedRoute>
      <div className="min-h-[100dvh] bg-slate-950 text-slate-100 flex">
        <Navbar />

        <main className="flex-1 px-6 py-8 overflow-y-auto">
          <div className="max-w-5xl mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </ProtectedRoute>
  );
}
