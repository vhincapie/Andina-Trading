import { NavLink, Outlet, useLocation } from "react-router-dom";

export default function CatalogosLayout() {
  const { pathname } = useLocation();
  const base = "/catalogos";

  if (pathname === "/") {
    return (
      <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
        <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
          <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
            Catálogos
          </h2>
          <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-6"></div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
            <a
              className="rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur px-4 py-3 text-center hover:bg-slate-900/70 transition"
              href={`${base}/paises`}
            >
              Países
            </a>
            <a
              className="rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur px-4 py-3 text-center hover:bg-slate-900/70 transition"
              href={`${base}/ciudades`}
            >
              Ciudades
            </a>
            <a
              className="rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur px-4 py-3 text-center hover:bg-slate-900/70 transition"
              href={`${base}/situaciones`}
            >
              Situaciones Económicas
            </a>
          </div>
        </div>
      </div>
    );
  }

  return <Outlet />;
}
