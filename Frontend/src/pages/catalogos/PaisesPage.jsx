import { useEffect, useState } from "react";
import {
  crearPais,
  listarPaises,
  listarSituaciones,
} from "../../api/catalogosService";
import { getApiErrorMessage } from "../../utils/errorMessage";

export default function PaisesPage() {
  const [nombre, setNombre] = useState("");
  const [codigoIso3, setCodigoIso3] = useState("");
  const [situacionId, setSituacionId] = useState("");

  const [loadingList, setLoadingList] = useState(false);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState(null);
  const [okMsg, setOkMsg] = useState(null);

  const [paises, setPaises] = useState([]);
  const [situaciones, setSituaciones] = useState([]);

  useEffect(() => {
    if (!error && !okMsg) return;
    const t = setTimeout(() => {
      setError(null);
      setOkMsg(null);
    }, 7000);
    return () => clearTimeout(t);
  }, [error, okMsg]);

  const resetAlerts = () => {
    setError(null);
    setOkMsg(null);
  };

  const validate = () => {
    const n = (nombre || "").trim();
    const c = (codigoIso3 || "").trim().toUpperCase();
    if (!n) return "El nombre es obligatorio.";
    if (!c) return "El código ISO3 es obligatorio.";
    if (c.length !== 3)
      return "El código ISO3 debe tener exactamente 3 letras.";
    if (!/^[A-Z]{3}$/.test(c))
      return "El código ISO3 solo debe contener letras A-Z.";
    if (!situacionId) return "La situación económica es obligatoria.";
    return null;
  };

  const loadData = async () => {
    setLoadingList(true);
    setError(null);
    try {
      const [paisesData, situacionesData] = await Promise.all([
        listarPaises(),
        listarSituaciones(),
      ]);
      setPaises(Array.isArray(paisesData) ? paisesData : []);
      setSituaciones(Array.isArray(situacionesData) ? situacionesData : []);
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const parsed = getApiErrorMessage(e);
      const msg =
        parsed ||
        (status === 401
          ? "Sesión inválida o expirada."
          : "No se pudieron cargar países o situaciones económicas.");
      setError(msg);
    } finally {
      setLoadingList(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const onSubmit = async (e) => {
    e.preventDefault();
    resetAlerts();

    const validation = validate();
    if (validation) {
      setError(validation);
      return;
    }

    const payload = {
      nombre: nombre.trim(),
      codigoIso3: codigoIso3.trim().toUpperCase(),
      situacionEconomicaDTO: { id: Number(situacionId) },
    };

    setCreating(true);
    try {
      await crearPais(payload);
      setOkMsg("País creado correctamente.");
      setNombre("");
      setCodigoIso3("");
      setSituacionId("");
      await loadData();
    } catch (e) {
      console.error(e);
      const status = e?.response?.status;
      const parsed = getApiErrorMessage(e);
      const msg =
        parsed ||
        (status === 409 && "Ya existe un país con ese código ISO3 o nombre.") ||
        (status === 400 && "Datos inválidos.") ||
        (status === 401 && "Sesión inválida o expirada.") ||
        "No se pudo crear el país.";
      setError(msg);
    } finally {
      setCreating(false);
    }
  };

  return (
    <div className="container" style={{ maxWidth: 720, margin: "0 auto" }}>
      <h2 style={{ margin: "16px 0" }}>Países</h2>

      <div
        style={{
          border: "1px solid #ddd",
          borderRadius: 8,
          padding: 16,
          marginBottom: 24,
        }}
      >
        <h4 style={{ marginTop: 0 }}>Crear país</h4>

        {error && (
          <div
            style={{
              background: "#fde2e2",
              border: "1px solid #f5c2c7",
              color: "#842029",
              padding: "10px 12px",
              borderRadius: 6,
              marginBottom: 12,
            }}
          >
            <strong>Error:</strong> {error}
          </div>
        )}

        {okMsg && (
          <div
            style={{
              background: "#e2f7e8",
              border: "1px solid #b6e3c1",
              color: "#0f5132",
              padding: "10px 12px",
              borderRadius: 6,
              marginBottom: 12,
            }}
          >
            {okMsg}
          </div>
        )}

        <form onSubmit={onSubmit}>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: "block", marginBottom: 6 }}>Nombre</label>
            <input
              type="text"
              value={nombre}
              onChange={(e) => setNombre(e.target.value)}
              placeholder="Ecuador"
              className="input"
              style={{
                width: "100%",
                padding: "10px 12px",
                borderRadius: 6,
                border: "1px solid #bbb",
              }}
              required
            />
          </div>

          <div style={{ marginBottom: 12 }}>
            <label style={{ display: "block", marginBottom: 6 }}>
              Código ISO3
            </label>
            <input
              type="text"
              value={codigoIso3}
              onChange={(e) => setCodigoIso3(e.target.value.toUpperCase())}
              placeholder="ECU"
              maxLength={3}
              className="input"
              style={{
                width: "100%",
                padding: "10px 12px",
                borderRadius: 6,
                border: "1px solid #bbb",
                textTransform: "uppercase",
              }}
              required
            />
          </div>

          <div style={{ marginBottom: 16 }}>
            <label style={{ display: "block", marginBottom: 6 }}>
              Situación Económica <span style={{ color: "#d00" }}>*</span>
            </label>
            <select
              value={situacionId}
              onChange={(e) => setSituacionId(e.target.value)}
              required
              style={{
                width: "100%",
                padding: "10px 12px",
                borderRadius: 6,
                border: "1px solid #bbb",
                background: "white",
              }}
            >
              <option value="">— Seleccionar —</option>
              {situaciones.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.nombre}
                </option>
              ))}
            </select>
            {situacionId && (
              <small style={{ display: "block", marginTop: 6, color: "#555" }}>
                {
                  situaciones.find((x) => String(x.id) === String(situacionId))
                    ?.descripcion
                }
              </small>
            )}
          </div>

          <button
            type="submit"
            disabled={creating}
            style={{
              background: creating ? "#6ea8fe" : "#0d6efd",
              color: "white",
              border: "none",
              padding: "10px 16px",
              borderRadius: 8,
              cursor: creating ? "not-allowed" : "pointer",
            }}
          >
            {creating ? "Creando..." : "Crear"}
          </button>
        </form>
      </div>

      <div
        style={{
          border: "1px solid #ddd",
          borderRadius: 8,
          padding: 16,
          marginBottom: 24,
        }}
      >
        <h4 style={{ marginTop: 0 }}>Listado</h4>

        {loadingList ? (
          <p>Cargando países...</p>
        ) : paises.length === 0 ? (
          <p>No hay países registrados.</p>
        ) : (
          <ul style={{ listStyle: "none", paddingLeft: 0, margin: 0 }}>
            {paises.map((p) => (
              <li
                key={p.id ?? `${p.nombre}-${p.codigoIso3}`}
                style={{
                  border: "1px solid #eee",
                  padding: "10px 12px",
                  borderRadius: 6,
                  marginBottom: 8,
                }}
              >
                <strong>{p.nombre}</strong> · {p.codigoIso3?.toUpperCase()}
                {p.situacionEconomicaDTO?.nombre && (
                  <div style={{ color: "#555", marginTop: 4 }}>
                    <small>
                      Situación: {p.situacionEconomicaDTO.nombre}
                      {p.situacionEconomicaDTO.descripcion
                        ? ` — ${p.situacionEconomicaDTO.descripcion}`
                        : ""}
                    </small>
                  </div>
                )}
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
