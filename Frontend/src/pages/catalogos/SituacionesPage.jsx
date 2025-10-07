import { useEffect, useState } from "react";
import { crearSituacion, listarSituaciones } from "../../api/catalogosService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

export default function SituacionesEconomicasPage() {
  const [items, setItems] = useState([]);
  const [form, setForm] = useState({ nombre: "", descripcion: "" });

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
      const situaciones = await listarSituaciones();
      setItems(situaciones);
    } catch (e) {
      console.error(e);
      setError(
        e?.response?.data?.message ||
          "No se pudieron cargar las situaciones económicas."
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
      await crearSituacion({
        nombre: form.nombre.trim(),
        descripcion: form.descripcion.trim() || undefined,
      });
      setOk("Situación económica creada correctamente.");
      setForm({ nombre: "", descripcion: "" });
      await load();
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const msg =
        e?.response?.data?.message ||
        (status === 409 &&
          "Ya existe una situación económica con ese nombre.") ||
        (status === 400 && "Datos inválidos.") ||
        "No se pudo crear la situación económica.";
      setError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-semibold">Situaciones Económicas</h2>

      <form
        onSubmit={onSubmit}
        className="border rounded p-4 space-y-3 bg-white"
      >
        <h3 className="font-semibold">Crear situación económica</h3>

        <ErrorAlert message={error} onClose={() => setError("")} />
        <SuccessAlert message={ok} onClose={() => setOk("")} />

        <div className="grid gap-3">
          <input
            className="border p-2 rounded"
            placeholder="Nombre"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            required
          />
          <textarea
            className="border p-2 rounded"
            placeholder="Descripción (opcional)"
            rows={3}
            value={form.descripcion}
            onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
          />
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
            {items.map((s) => (
              <li key={s.id} className="border rounded p-2">
                <div className="font-medium">{s.nombre}</div>
                {s.descripcion && (
                  <div className="text-sm text-gray-600">{s.descripcion}</div>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
