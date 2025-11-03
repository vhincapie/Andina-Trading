import { useEffect, useMemo, useState } from "react";
import {
  listarComisionistas,
  registrarComisionista,
} from "../../api/comisionistaService";
import { listarPaises, listarCiudades } from "../../api/catalogosService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

function fmt(d) {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

export default function ComisionistasPage() {
  const [list, setList] = useState([]);
  const [paises, setPaises] = useState([]);
  const [ciudades, setCiudades] = useState([]);

  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    tipoDocumento: "",
    numeroDocumento: "",
    correo: "",
    contrasena: "",
    fechaNacimiento: "",
    paisId: "",
    ciudadId: "",
    aniosExperiencia: "",
  });

  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState("");
  const [ok, setOk] = useState("");

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 7000);
    return () => clearTimeout(t);
  }, [error, ok]);

  const ciudadesFiltradas = useMemo(() => {
    if (!form.paisId) return [];
    return ciudades.filter(
      (c) => String(c.paisDTO?.id) === String(form.paisId)
    );
  }, [ciudades, form.paisId]);

  const paisNombreById = useMemo(() => {
    const m = {};
    for (const p of paises) m[String(p.id)] = p.nombre;
    return m;
  }, [paises]);

  const ciudadNombreById = useMemo(() => {
    const m = {};
    for (const c of ciudades) m[String(c.id)] = c.nombre;
    return m;
  }, [ciudades]);

  const maxBirthDate = useMemo(() => {
    const today = new Date();
    return fmt(
      new Date(today.getFullYear() - 18, today.getMonth(), today.getDate())
    );
  }, []);

  const minBirthDate = "1900-01-01";

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const [items, ps, cs] = await Promise.all([
        listarComisionistas(),
        listarPaises(),
        listarCiudades(),
      ]);
      setList(Array.isArray(items) ? items : []);
      setPaises(ps || []);
      setCiudades(cs || []);
    } catch (e) {
      console.error(e);
      const msg =
        e?.response?.data?.message ||
        (e?.response?.status === 401 && "Sesión inválida o expirada.") ||
        "No se pudieron cargar comisionistas o catálogos.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const onChange = (e) => {
    const { name, value } = e.target;
    if (name === "paisId") {
      setForm((f) => ({ ...f, paisId: value, ciudadId: "" }));
      return;
    }
    setForm((f) => ({ ...f, [name]: value }));
  };

  const validate = () => {
    if (!form.nombre.trim()) return "El nombre es obligatorio.";
    if (!form.apellido.trim()) return "El apellido es obligatorio.";
    if (!form.tipoDocumento) return "Selecciona un tipo de documento.";
    if (!form.numeroDocumento.trim())
      return "El número de documento es obligatorio.";
    if (!form.correo.trim()) return "El correo es obligatorio.";
    if ((form.contrasena || "").length < 8)
      return "La contraseña debe tener al menos 8 caracteres.";
    if (!form.paisId) return "Selecciona un país.";
    if (!form.ciudadId) return "Selecciona una ciudad.";
    const anios = Number(form.aniosExperiencia);
    if (!Number.isFinite(anios) || anios < 0)
      return "Años de experiencia debe ser un número mayor o igual a 0.";
    if (form.fechaNacimiento) {
      if (form.fechaNacimiento < minBirthDate)
        return `La fecha de nacimiento no puede ser anterior a ${minBirthDate}.`;
      if (form.fechaNacimiento > maxBirthDate)
        return "El comisionista debe ser mayor de 18 años.";
    } else {
      return "La fecha de nacimiento es obligatoria.";
    }
    return null;
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    const v = validate();
    if (v) return setError(v);

    setCreating(true);
    try {
      const payload = {
        nombre: form.nombre.trim(),
        apellido: form.apellido.trim(),
        tipoDocumento: form.tipoDocumento,
        numeroDocumento: form.numeroDocumento.trim(),
        correo: form.correo.trim().toLowerCase(),
        contrasena: form.contrasena,
        fechaNacimiento: form.fechaNacimiento,
        paisId: Number(form.paisId),
        ciudadId: Number(form.ciudadId),
        aniosExperiencia: Number(form.aniosExperiencia),
      };

      await registrarComisionista(payload);
      setOk("Comisionista registrado correctamente.");
      setForm({
        nombre: "",
        apellido: "",
        tipoDocumento: "",
        numeroDocumento: "",
        correo: "",
        contrasena: "",
        fechaNacimiento: "",
        paisId: "",
        ciudadId: "",
        aniosExperiencia: "",
      });
      await load();
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const msg =
        e?.response?.data?.message ||
        (status === 409 && "El correo o documento ya están registrados.") ||
        (status === 400 && "Datos inválidos en el registro.") ||
        "No se pudo registrar el comisionista.";
      setError(msg);
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-5xl mx-auto px-4 md:px-6 py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Comisionistas
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        <form
          onSubmit={onSubmit}
          className="bg-slate-900/50 border border-slate-800 rounded-2xl p-6 shadow-xl ring-1 ring-white/5 backdrop-blur space-y-4"
        >
          <h3 className="text-lg font-semibold text-slate-100">
            Registrar comisionista
          </h3>
          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />

          <div className="grid gap-4 md:gap-6 md:grid-cols-3">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Nombre
              </label>
              <input
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="nombre"
                placeholder="Nombre"
                value={form.nombre}
                onChange={onChange}
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Apellido
              </label>
              <input
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="apellido"
                placeholder="Apellido"
                value={form.apellido}
                onChange={onChange}
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Fecha de nacimiento
              </label>
              <input
                type="date"
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="fechaNacimiento"
                value={form.fechaNacimiento}
                onChange={onChange}
                min={minBirthDate}
                max={maxBirthDate}
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Tipo de documento
              </label>
              <select
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="tipoDocumento"
                value={form.tipoDocumento}
                onChange={onChange}
                required
              >
                <option value="">Selecciona…</option>
                <option value="CC">Cédula de Ciudadanía</option>
                <option value="CE">Cédula de Extranjería</option>
                <option value="PAS">Pasaporte</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Número de documento
              </label>
              <input
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="numeroDocumento"
                placeholder="Número"
                value={form.numeroDocumento}
                onChange={onChange}
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Correo
              </label>
              <input
                type="email"
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="correo"
                placeholder="correo@dominio.com"
                value={form.correo}
                onChange={onChange}
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Contraseña
              </label>
              <input
                type="password"
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="contrasena"
                placeholder="Mínimo 8 caracteres"
                value={form.contrasena}
                onChange={onChange}
                required
                minLength={8}
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Años de experiencia
              </label>
              <input
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 placeholder:text-slate-500 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
                name="aniosExperiencia"
                placeholder="0"
                value={form.aniosExperiencia}
                onChange={onChange}
                inputMode="numeric"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                País
              </label>
              <select
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition"
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
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Ciudad
              </label>
              <select
                className="w-full bg-slate-950/60 ring-1 ring-slate-800 rounded-lg p-3 text-slate-100 focus:ring-emerald-400/70 focus:bg-slate-900/70 transition disabled:opacity-60"
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
            disabled={creating}
            className="bg-emerald-500/90 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium shadow-sm disabled:opacity-60 transition"
          >
            {creating ? "Registrando..." : "Registrar"}
          </button>
        </form>

        <div className="mt-6 bg-slate-900/50 border border-slate-800 rounded-2xl p-6 shadow-xl ring-1 ring-white/5 backdrop-blur">
          <h3 className="text-lg font-semibold text-slate-100 mb-3">
            Listado de comisionistas
          </h3>
          {loading ? (
            <p className="text-slate-400">Cargando...</p>
          ) : list.length === 0 ? (
            <p className="text-slate-400">No hay comisionistas registrados.</p>
          ) : (
            <ul className="space-y-2">
              {list.map((c) => {
                const paisNombre = paisNombreById[String(c.paisId)] ?? "—";
                const ciudadNombre =
                  ciudadNombreById[String(c.ciudadId)] ?? "—";
                return (
                  <li
                    key={c.id}
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 p-3 hover:bg-slate-900/40 transition"
                  >
                    <div className="font-medium text-slate-100">
                      {c.nombre} {c.apellido} · {c.correo}
                    </div>
                    <div className="text-sm text-slate-400">
                      Doc: {c.tipoDocumento} {c.numeroDocumento} · Nacimiento:{" "}
                      {c.fechaNacimiento || "—"} · País: {paisNombre} · Ciudad:{" "}
                      {ciudadNombre} · Experiencia: {c.aniosExperiencia} año(s)
                    </div>
                  </li>
                );
              })}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
