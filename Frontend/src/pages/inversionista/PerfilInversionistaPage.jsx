import { useEffect, useMemo, useState } from "react";
import {
  getMiPerfil,
  actualizarMiPerfil,
} from "../../api/inversionistaService";
import { listarPaises, listarCiudades } from "../../api/catalogosService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";
import { useAuth } from "../../context/AuthContext";

function normalizePerfil(raw = {}, fallbackCorreo = "", fallbackRol = "") {
  const get = (obj, ...keys) =>
    keys.reduce(
      (v, k) => (v !== undefined && v !== null ? v : obj?.[k]),
      undefined
    );

  const correo = get(raw, "correo", "email", "username") ?? fallbackCorreo;
  const nombre =
    get(raw, "nombre", "nombres", "firstName", "primerNombre") ?? "";
  const apellido =
    get(raw, "apellido", "apellidos", "lastName", "primerApellido") ?? "";
  const fechaNacimiento = get(raw, "fechaNacimiento", "birthDate") ?? "";
  const tipoDocumento = get(raw, "tipoDocumento", "documentType") ?? "";
  const numeroDocumento =
    get(raw, "numeroDocumento", "documentNumber", "documento") ?? "";

  const paisId =
    get(raw, "paisId") ?? get(raw, "paisDTO", "pais", "country")?.id ?? "";
  const ciudadId =
    get(raw, "ciudadId") ?? get(raw, "ciudadDTO", "ciudad", "city")?.id ?? "";

  const paisNombre =
    get(raw, "paisDTO", "pais", "country")?.nombre ??
    get(raw, "paisNombre", "countryName");
  const ciudadNombre =
    get(raw, "ciudadDTO", "ciudad", "city")?.nombre ??
    get(raw, "ciudadNombre", "cityName");

  const rolRaw = get(raw, "rol", "role") ?? fallbackRol;
  const rol = String(rolRaw || "").toUpperCase();

  return {
    correo,
    nombre,
    apellido,
    fechaNacimiento,
    tipoDocumento,
    numeroDocumento,
    paisId: paisId || "",
    ciudadId: ciudadId || "",
    paisNombre,
    ciudadNombre,
    rol,
  };
}

export default function PerfilInversionistaPage() {
  const { user } = useAuth();
  const [perfil, setPerfil] = useState(null);
  const [paises, setPaises] = useState([]);
  const [ciudades, setCiudades] = useState([]);
  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    fechaNacimiento: "",
    tipoDocumento: "",
    numeroDocumento: "",
    paisId: "",
    ciudadId: "",
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  const ciudadesFiltradas = useMemo(() => {
    if (!form.paisId) return [];
    return ciudades.filter(
      (c) => String(c.paisDTO?.id) === String(form.paisId)
    );
  }, [ciudades, form.paisId]);

  useEffect(() => {
    (async () => {
      try {
        const [raw, ps, cs] = await Promise.all([
          getMiPerfil(),
          listarPaises(),
          listarCiudades(),
        ]);
        const norm = normalizePerfil(raw, user?.correo, user?.rol);
        setPerfil(norm);
        setPaises(ps || []);
        setCiudades(cs || []);
        setForm({
          nombre: norm.nombre,
          apellido: norm.apellido,
          fechaNacimiento: norm.fechaNacimiento || "",
          tipoDocumento: norm.tipoDocumento,
          numeroDocumento: norm.numeroDocumento,
          paisId: norm.paisId || "",
          ciudadId: norm.ciudadId || "",
        });
      } catch (e) {
        console.error(e);
        setError("No se pudo cargar la información del perfil.");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 6000);
    return () => clearTimeout(t);
  }, [error, ok]);

  const onChange = (e) => {
    const { name, value } = e.target;
    if (name === "paisId")
      setForm((f) => ({ ...f, paisId: value, ciudadId: "" }));
    else setForm((f) => ({ ...f, [name]: value }));
  };

  const validate = () => {
    if (!form.paisId) return "El país es obligatorio.";
    if (!form.ciudadId) return "La ciudad es obligatoria.";
    return null;
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    const v = validate();
    if (v) return setError(v);

    setSaving(true);
    try {
      const updatedRaw = await actualizarMiPerfil({
        paisId: Number(form.paisId),
        ciudadId: Number(form.ciudadId),
      });
      const normUpdated = normalizePerfil(
        updatedRaw,
        perfil?.correo,
        perfil?.rol
      );
      setPerfil(normUpdated);
      setOk("Ubicación actualizada correctamente.");
    } catch (e) {
      console.error(e);
      setError("No se pudo actualizar la ubicación.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p className="max-w-5xl mx-auto py-6">Cargando...</p>;

  const paisNombre =
    perfil?.paisNombre ||
    paises.find((p) => String(p.id) === String(form.paisId))?.nombre ||
    "—";
  const ciudadNombre =
    perfil?.ciudadNombre ||
    ciudades.find((c) => String(c.id) === String(form.ciudadId))?.nombre ||
    "—";

  return (
    <div className="max-w-3xl mx-auto px-4 py-6 space-y-6">
      <h2 className="text-2xl font-semibold">Mi perfil</h2>

      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <div className="bg-white border rounded p-4 space-y-1">
        <p>
          <b>Correo:</b> {perfil?.correo || "—"}
        </p>
        <p>
          <b>Nombre:</b> {perfil?.nombre || "—"}
        </p>
        <p>
          <b>Apellido:</b> {perfil?.apellido || "—"}
        </p>
        <p>
          <b>Fecha de nacimiento:</b> {perfil?.fechaNacimiento || "—"}
        </p>
        <p>
          <b>Tipo de documento:</b> {perfil?.tipoDocumento || "—"}
        </p>
        <p>
          <b>Número de documento:</b> {perfil?.numeroDocumento || "—"}
        </p>
        <p>
          <b>País:</b> {paisNombre}
        </p>
        <p>
          <b>Ciudad:</b> {ciudadNombre}
        </p>
        <p>
          <b>Rol:</b>{" "}
          {perfil?.rol || String(user?.rol || "").toUpperCase() || "—"}
        </p>
      </div>

      <form
        onSubmit={onSubmit}
        className="bg-white border rounded p-4 space-y-3"
      >
        <div className="grid gap-3 md:grid-cols-2">
          <div>
            <label className="block text-sm mb-1">Nombre</label>
            <input
              className="border p-2 rounded bg-gray-100"
              name="nombre"
              value={form.nombre}
              readOnly
            />
          </div>
          <div>
            <label className="block text-sm mb-1">Apellido</label>
            <input
              className="border p-2 rounded bg-gray-100"
              name="apellido"
              value={form.apellido}
              readOnly
            />
          </div>
          <div>
            <label className="block text-sm mb-1">Tipo de documento</label>
            <select
              className="border p-2 rounded bg-gray-100"
              name="tipoDocumento"
              value={form.tipoDocumento}
              disabled
            >
              <option value="">
                {form.tipoDocumento || "Tipo de documento"}
              </option>
              <option value="CC">Cédula de Ciudadanía</option>
              <option value="CE">Cédula de Extranjería</option>
              <option value="PAS">Pasaporte</option>
            </select>
          </div>
          <div>
            <label className="block text-sm mb-1">Número de documento</label>
            <input
              className="border p-2 rounded bg-gray-100"
              name="numeroDocumento"
              value={form.numeroDocumento}
              readOnly
            />
          </div>
          <div>
            <label className="block text-sm mb-1">Fecha de nacimiento</label>
            <input
              type="date"
              className="border p-2 rounded bg-gray-100"
              name="fechaNacimiento"
              value={form.fechaNacimiento || ""}
              readOnly
            />
          </div>

          <div>
            <label className="block text-sm mb-1">País</label>
            <select
              className="border p-2 rounded"
              name="paisId"
              value={form.paisId}
              onChange={onChange}
              required
            >
              <option value="">Selecciona un país…</option>
              {paises.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nombre}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm mb-1">Ciudad</label>
            <select
              className="border p-2 rounded"
              name="ciudadId"
              value={form.ciudadId}
              onChange={onChange}
              required
              disabled={!form.paisId}
            >
              <option value="">
                {form.paisId
                  ? "Selecciona una ciudad…"
                  : "Selecciona primero el país"}
              </option>
              {ciudadesFiltradas.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.nombre}
                </option>
              ))}
            </select>
          </div>
        </div>

        <button
          type="submit"
          disabled={saving}
          className="bg-blue-600 text-white px-3 py-2 rounded disabled:opacity-60"
        >
          {saving ? "Guardando..." : "Actualizar ubicación"}
        </button>
      </form>
    </div>
  );
}
