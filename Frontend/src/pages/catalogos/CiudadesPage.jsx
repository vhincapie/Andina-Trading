import { useEffect, useState } from "react";
import {
  crearCiudad,
  listarCiudades,
  listarPaises,
} from "../../api/catalogosService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

export default function CiudadesPage() {
  const [items, setItems] = useState([]);
  const [paises, setPaises] = useState([]);
  const [form, setForm] = useState({ nombre: "", paisId: "" });

  const [error, setError] = useState("");
  const [ok, setOk] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 7000);
    return () => clearTimeout(t);
  }, [error, ok]);

  const load = async () => {
    setError("");
    setLoading(true);
    try {
      const [ciudades, paisesResp] = await Promise.all([
        listarCiudades(),
        listarPaises(),
      ]);
      setItems(ciudades);
      setPaises(paisesResp);
    } catch (e) {
      console.error(e);
      setError(
        e?.response?.data?.message || "No se pudieron cargar ciudades o países."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    setSubmitting(true);
    try {
      await crearCiudad({
        nombre: form.nombre.trim(),
        estado: "ACTIVO",
        paisDTO: { id: Number(form.paisId) },
      });
      setOk("Ciudad creada correctamente.");
      setForm({ nombre: "", paisId: "" });
      await load();
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const msg =
        e?.response?.data?.message ||
        (status === 409 &&
          "Ya existe una ciudad con ese nombre para ese país.") ||
        (status === 400 && "Datos inválidos.") ||
        "No se pudo crear la ciudad.";
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-semibold">Ciudades</h2>

      <form
        onSubmit={onSubmit}
        className="border rounded p-4 space-y-3 bg-white"
      >
        <h3 className="font-semibold">Crear ciudad</h3>

        <ErrorAlert message={error} onClose={() => setError("")} />
        <SuccessAlert message={ok} onClose={() => setOk("")} />

        <div className="grid gap-3 md:grid-cols-3">
          <input
            className="border p-2 rounded"
            placeholder="Nombre"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            required
          />
          <select
            className="border p-2 rounded"
            value={form.paisId}
            onChange={(e) => setForm({ ...form, paisId: e.target.value })}
            required
          >
            <option value="">Selecciona país</option>
            {paises.map((p) => (
              <option key={p.id} value={p.id}>
                {p.nombre}
              </option>
            ))}
          </select>
        </div>
        <button
          className="bg-blue-600 text-white px-3 py-2 rounded disabled:opacity-50"
          disabled={submitting}
        >
          {submitting ? "Creando..." : "Crear"}
        </button>
      </form>

      <div className="bg-white border rounded p-4">
        <h3 className="font-semibold mb-2">Listado</h3>
        {loading ? (
          <p className="text-gray-600">Cargando...</p>
        ) : (
          <ul className="space-y-2">
            {items.map((c) => (
              <li key={c.id} className="border rounded p-2">
                <div className="font-medium">
                  {c.nombre} · {c.estado}
                </div>
                <div className="text-sm text-gray-600">
                  País: {c.paisDTO?.nombre}
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
