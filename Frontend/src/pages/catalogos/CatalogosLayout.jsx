import { NavLink, Outlet, useLocation } from "react-router-dom";

export default function CatalogosLayout() {
  const { pathname } = useLocation();
  const base = "/catalogos";

  if (pathname === "/") {
    return (
      <div className="space-y-4">
        <h2 className="text-2xl font-semibold">Catálogos</h2>
        <div className="flex gap-3">
          <a
            className="px-3 py-2 rounded border hover:bg-gray-50"
            href={`${base}/paises`}
          >
            Países
          </a>
          <a
            className="px-3 py-2 rounded border hover:bg-gray-50"
            href={`${base}/ciudades`}
          >
            Ciudades
          </a>
          <a
            className="px-3 py-2 rounded border hover:bg-gray-50"
            href={`${base}/situaciones`}
          >
            Situaciones Económicas
          </a>
        </div>
      </div>
    );
  }


  return <Outlet />;
}
