import { useEffect, useMemo, useState } from "react";
import { listarMisContratosComisionista } from "../../api/contratoService";
import ErrorAlert from "../../components/alerts/ErrorAlert";

const TABS = [
  { key: "ACTIVOS", label: "Activos" },
  { key: "FINALIZADOS", label: "Finalizados" },
  { key: "TODOS", label: "Todos" },
];

export default function ContratosComisionistaPage() {
  const [items, setItems] = useState([]);
  const [tab, setTab] = useState("ACTIVOS");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = async () => {
    setError("");
    setLoading(true);
    try {
      const data = await listarMisContratosComisionista();
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      const msg =
        e?.response?.data?.message ||
        (e?.response?.status === 401 && "Sesión inválida o expirada.") ||
        "No fue posible cargar tus contratos.";
      setItems([]);
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const activos = useMemo(
    () => items.filter((c) => String(c.estado).toUpperCase() === "ACTIVO"),
    [items]
  );

  const finalizados = useMemo(
    () => items.filter((c) => String(c.estado).toUpperCase() !== "ACTIVO"),
    [items]
  );

  const currentList = useMemo(() => {
    if (tab === "ACTIVOS") return activos;
    if (tab === "FINALIZADOS") return finalizados;
    return items;
  }, [tab, activos, finalizados, items]);

  const renderLista = (list) => (
    <ul className="space-y-2">
      {list.map((c) => (
        <li key={c.id} className="border rounded p-3">
          <div className="font-medium">
            Inversionista: {c.inversionistaNombreCompleto || "—"}
            {c.inversionistaDocumento ? ` (${c.inversionistaDocumento})` : ""}
          </div>
          <div className="text-sm text-gray-700 space-y-0.5 mt-1">
            <p>
              <b>Estado:</b>{" "}
              <span
                className={`${
                  String(c.estado).toUpperCase() === "ACTIVO"
                    ? "text-green-600"
                    : "text-red-600"
                } font-semibold`}
              >
                {c.estado}
              </span>
            </p>
            <p>
              <b>Moneda:</b> {c.moneda || "—"}
            </p>
            <p>
              <b>Porcentaje cobro:</b> {c.porcentajeCobroAplicado ?? "—"}%
            </p>
            <p>
              <b>Fecha inicio:</b>{" "}
              {c.fechaInicio ? new Date(c.fechaInicio).toLocaleString() : "—"}
            </p>
            {c.fechaFin && (
              <p>
                <b>Fecha fin:</b> {new Date(c.fechaFin).toLocaleString()}
              </p>
            )}
          </div>
        </li>
      ))}
    </ul>
  );

  return (
    <div className="max-w-3xl mx-auto px-4 py-6 space-y-4">
      <h2 className="text-2xl font-semibold">
        Mis contratos con inversionistas
      </h2>
      <ErrorAlert message={error} onClose={() => setError("")} />

      <div className="flex items-center justify-between">
        <div className="inline-flex rounded border overflow-hidden">
          {TABS.map((t) => {
            const isActive = tab === t.key;
            const count =
              t.key === "ACTIVOS"
                ? activos.length
                : t.key === "FINALIZADOS"
                ? finalizados.length
                : items.length;
            return (
              <button
                key={t.key}
                onClick={() => setTab(t.key)}
                className={`px-3 py-2 text-sm ${
                  isActive ? "bg-gray-900 text-white" : "bg-white text-gray-800"
                } ${t.key !== "ACTIVOS" ? "border-l" : ""}`}
              >
                {t.label} ({count})
              </button>
            );
          })}
        </div>

        <button
          onClick={load}
          className="px-3 py-2 border rounded hover:bg-gray-50"
          disabled={loading}
        >
          {loading ? "Actualizando..." : "Actualizar"}
        </button>
      </div>

      <div className="bg-white border rounded p-4">
        {loading ? (
          <p className="text-gray-600">Cargando contratos...</p>
        ) : currentList.length === 0 ? (
          <p className="text-gray-600">
            {tab === "ACTIVOS"
              ? "No tienes contratos activos."
              : tab === "FINALIZADOS"
              ? "No tienes contratos finalizados/cancelados."
              : "No hay contratos para mostrar."}
          </p>
        ) : (
          renderLista(currentList)
        )}
      </div>
    </div>
  );
}
