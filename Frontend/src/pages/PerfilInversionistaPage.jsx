import { useEffect, useMemo, useState } from "react";
import { getMiPerfil, actualizarMiPerfil } from "../api/inversionistaService";
import { listarPaises, listarCiudades } from "../api/catalogosService";
import { listarComisionistas } from "../api/comisionistaService";
import {
  registrarContrato,
  getMiContratoActivo,
  cancelarMiContratoActivo,
} from "../api/contratoService";
import ErrorAlert from "../components/alerts/ErrorAlert";
import SuccessAlert from "../components/alerts/SuccessAlert";
import { useAuth } from "../context/AuthContext";
import Modal from "../components/Modal";

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

  const fechaNacimiento =
    get(raw, "fechaNacimiento", "fecha_nacimiento", "birthDate") ?? "";

  const tipoDocumento =
    get(raw, "tipoDocumento", "tipo_documento", "documentType") ?? "";

  const numeroDocumento =
    get(
      raw,
      "numeroDocumento",
      "numero_documento",
      "documentNumber",
      "documento"
    ) ?? "";

  const paisId =
    get(raw, "paisId") ??
    get(raw, "paisDTO", "pais", "country")?.id ??
    undefined;

  const ciudadId =
    get(raw, "ciudadId") ??
    get(raw, "ciudadDTO", "ciudad", "city")?.id ??
    undefined;

  const paisNombre =
    get(raw, "paisDTO", "pais", "country")?.nombre ??
    get(raw, "paisNombre", "countryName") ??
    undefined;

  const ciudadNombre =
    get(raw, "ciudadDTO", "ciudad", "city")?.nombre ??
    get(raw, "ciudadNombre", "cityName") ??
    undefined;

  const rolRaw = get(raw, "rol", "role") ?? fallbackRol;
  const rol = String(rolRaw || "").toUpperCase();

  return {
    correo,
    nombre,
    apellido,
    fechaNacimiento,
    tipoDocumento,
    numeroDocumento,
    paisId: paisId ?? "",
    ciudadId: ciudadId ?? "",
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

  // ---- Contratos UI state ----
  const [contrato, setContrato] = useState(null);
  const [comisionistas, setComisionistas] = useState([]);
  const [selectedComisionistaId, setSelectedComisionistaId] = useState("");
  const [moneda, setMoneda] = useState("COP");
  const [observaciones, setObservaciones] = useState("");
  const [contratoLoading, setContratoLoading] = useState(true);
  const [contratoSaving, setContratoSaving] = useState(false);

  // Modal de términos
  const [showTerms, setShowTerms] = useState(false);
  const [acceptedTerms, setAcceptedTerms] = useState(false);

  // Porcentaje mostrado en términos (opcionalmente desde .env)
  const porcentajeDefault =
    Number(import.meta.env.VITE_CONTRATOS_PORCENTAJE_DEFAULT ?? 2.5);

  const ciudadesFiltradas = useMemo(() => {
    if (!form.paisId) return [];
    return ciudades.filter(
      (c) => String(c.paisDTO?.id) === String(form.paisId)
    );
  }, [ciudades, form.paisId]);

  useEffect(() => {
    (async () => {
      try {
        // Perfil (inversionista) + Catálogos
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
        console.error("PERFIL ERROR =>", {
          status: e?.response?.status,
          data: e?.response?.data,
          url: (e?.config?.baseURL || "") + (e?.config?.url || ""),
        });
        setError("No se pudo cargar la información del perfil.");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  // Cargar contrato activo + lista de comisionistas
  useEffect(() => {
    (async () => {
      try {
        const [cont, lista] = await Promise.all([
          getMiContratoActivo().catch(() => null),
          listarComisionistas().catch(() => []),
        ]);
        setContrato(cont);
        setComisionistas(lista || []);
      } catch (e) {
        console.error("CONTRATO/COMISIONISTAS ERROR =>", e);
      } finally {
        setContratoLoading(false);
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
    if (name === "paisId") {
      setForm((f) => ({ ...f, paisId: value, ciudadId: "" }));
    } else {
      setForm((f) => ({ ...f, [name]: value }));
    }
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
      const payload = {
        paisId: Number(form.paisId),
        ciudadId: Number(form.ciudadId),
      };
      const updatedRaw = await actualizarMiPerfil(payload);

      const normUpdated = normalizePerfil(
        updatedRaw,
        perfil?.correo,
        perfil?.rol
      );
      setPerfil(normUpdated);

      setOk("Ubicación actualizada correctamente.");
    } catch (e) {
      console.error("UPDATE ERROR =>", {
        status: e?.response?.status,
        data: e?.response?.data,
        url: (e?.config?.baseURL || "") + (e?.config?.url || ""),
      });
      setError("No se pudo actualizar la ubicación.");
    } finally {
      setSaving(false);
    }
  };

  // Texto de términos con placeholders dinámicos
  const buildTerminos = () => {
    const raw = `
ANDINA TRADING S.A.S. – TÉRMINOS Y CONDICIONES DEL CONTRATO DE COMISIONAMIENTO

1. Partes.
Intervienen: (i) ANDINA TRADING S.A.S. (“Andina Trading”), en calidad de comisionista, a través del comisionista seleccionado por el inversionista, y (ii) el Inversionista identificado en la orden de registro realizada en la plataforma.

2. Objeto.
El Comisionista realizará, por cuenta y riesgo del Inversionista, actividades de intermediación y ejecución de órdenes de inversión conforme a las instrucciones del Inversionista y a las políticas internas de Andina Trading.

3. Honorarios.
El Inversionista reconoce y acepta un honorario estándar equivalente al {{PORCENTAJE}}% sobre el valor administrado y/o resultados según aplique, cobrado con la periodicidad definida por Andina Trading. Este porcentaje es corporativo y aplica de manera uniforme para todos los inversionistas salvo comunicación posterior por escrito.

4. Moneda y cargos.
La moneda del contrato será {{MONEDA}}. El Inversionista asume costos, impuestos, comisiones bancarias, cambiarias y demás cargos aplicables por terceros.

5. Vigencia.
El contrato inicia en la Fecha de Inicio indicada por el Inversionista y tendrá vigencia hasta la Fecha de Fin (si fue indicada) o hasta su terminación conforme a la Cláusula 12.

6. Perfil y facultades.
El Comisionista actuará siguiendo las instrucciones del Inversionista y las normas aplicables. No garantiza resultados. El Inversionista declara comprender los riesgos del mercado y que los precios de los instrumentos financieros pueden fluctuar.

7. Información y reportes.
Andina Trading pondrá a disposición del Inversionista la información operativa razonable sobre sus posiciones y movimientos, a través de los medios tecnológicos habilitados.

8. Confidencialidad.
La información de cada parte se mantendrá confidencial y solo se divulgará cuando sea exigido por ley o autoridad competente.

9. Protección de datos.
El tratamiento de datos personales se realizará conforme a la política de privacidad de Andina Trading disponible en sus canales oficiales. El Inversionista autoriza dicho tratamiento para la ejecución de este contrato.

10. Prevención de fraude y cumplimiento.
Andina Trading podrá realizar verificaciones KYC/AML y suspender operaciones ante señales de riesgo o requerimientos regulatorios.

11. Responsabilidad.
Andina Trading y el Comisionista no serán responsables por pérdidas derivadas de fluctuaciones de mercado, fuerza mayor, fallas de terceros o decisiones del Inversionista.

12. Terminación.
Cualquiera de las partes podrá terminar el contrato en cualquier momento con aviso por los canales oficiales. La terminación no afecta las obligaciones de pago devengadas ni los procesos de cierre en curso.

13. Modificaciones.
Cualquier modificación de estos términos se comunicará por canales oficiales y regirá para contratos futuros. Los contratos ya celebrados conservan su texto y porcentaje aplicados al momento de su creación.

14. Ley y jurisdicción.
Este contrato se rige por las leyes del país de registro de Andina Trading. Las controversias se someterán a los jueces competentes del mismo domicilio, salvo pacto arbitral distinto.

15. Aceptación.
Al marcar “Acepto los términos” y registrar la orden en la plataforma, el Inversionista declara haber leído, entendido y aceptado íntegramente este contrato.
    `
      .replaceAll("{{PORCENTAJE}}", String(porcentajeDefault))
      .replaceAll("{{MONEDA}}", moneda);

    return raw.trim();
  };

  // ---- Acciones de contratos ----
  const onRegistrarContrato = async () => {
    setError("");
    setOk("");
    if (!selectedComisionistaId) {
      return setError("Debes seleccionar un comisionista.");
    }
    if (!acceptedTerms) {
      return setError("Debes aceptar los términos del contrato.");
    }
    setContratoSaving(true);
    try {
      const payload = {
        comisionistaId: Number(selectedComisionistaId),
        moneda,
        observaciones: observaciones?.trim() || "",
        aceptaTerminos: true,
      };
      const c = await registrarContrato(payload);
      setContrato(c);
      setOk("Contrato registrado con éxito.");
    } catch (e) {
      setError(
        e?.response?.data?.message || "No fue posible registrar el contrato."
      );
    } finally {
      setContratoSaving(false);
    }
  };

  const onCancelarContrato = async () => {
    setError("");
    setOk("");
    if (!contrato || contrato.estado !== "ACTIVO") return;
    setContratoSaving(true);
    try {
      const c = await cancelarMiContratoActivo();
      setContrato(c);
      setOk("Contrato cancelado.");
    } catch (e) {
      setError(
        e?.response?.data?.message || "No fue posible cancelar el contrato."
      );
    } finally {
      setContratoSaving(false);
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

      {/* Datos del perfil */}
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

      {/* Form actualizar ubicación */}
      <form onSubmit={onSubmit} className="bg-white border rounded p-4 space-y-3">
        <div className="grid gap-3 md:grid-cols-2">
          <input
            className="border p-2 rounded bg-gray-100"
            name="nombre"
            value={form.nombre}
            readOnly
            placeholder="Nombre"
          />
          <input
            className="border p-2 rounded bg-gray-100"
            name="apellido"
            value={form.apellido}
            readOnly
            placeholder="Apellido"
          />
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
          <input
            className="border p-2 rounded bg-gray-100"
            name="numeroDocumento"
            value={form.numeroDocumento}
            readOnly
            placeholder="Número de documento"
          />
          <input
            type="date"
            className="border p-2 rounded bg-gray-100"
            name="fechaNacimiento"
            value={form.fechaNacimiento || ""}
            readOnly
          />

          <select
            className="border p-2 rounded"
            name="paisId"
            value={form.paisId}
            onChange={onChange}
            required
          >
            <option value="">País</option>
            {paises.map((p) => (
              <option key={p.id} value={p.id}>
                {p.nombre}
              </option>
            ))}
          </select>

          <select
            className="border p-2 rounded"
            name="ciudadId"
            value={form.ciudadId}
            onChange={onChange}
            required
            disabled={!form.paisId}
          >
            <option value="">
              {form.paisId ? "Ciudad" : "Selecciona primero el país"}
            </option>
            {ciudadesFiltradas.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre}
              </option>
            ))}
          </select>
        </div>

        <button
          type="submit"
          disabled={saving}
          className="bg-blue-600 text-white px-3 py-2 rounded disabled:opacity-60"
        >
          {saving ? "Guardando..." : "Actualizar ubicación"}
        </button>
      </form>

      {/* Gestión de Contrato */}
      <section className="bg-white border rounded p-4 space-y-3">
        <h3 className="text-xl font-semibold">Contrato</h3>

        {contratoLoading ? (
          <p>Cargando contrato...</p>
        ) : contrato ? (
          <div className="space-y-1">
            <p>
              <b>Comisionista:</b>{" "}
              {contrato.comisionistaNombreCompleto || "—"}
            </p>
            <p>
              <b>Inversionista:</b>{" "}
              {contrato.inversionistaNombreCompleto || "—"}{" "}
              {contrato.inversionistaDocumento
                ? `(${contrato.inversionistaDocumento})`
                : ""}
            </p>
            <p>
              <b>Estado:</b> {contrato.estado}
            </p>
            <p>
              <b>Moneda:</b> {contrato.moneda}
            </p>
            <p>
              <b>Porcentaje cobro:</b> {contrato.porcentajeCobroAplicado}%</p>
            <p>
              <b>Fecha inicio:</b> {contrato.fechaInicio || "—"}
            </p>
            {!!contrato.fechaFin && (
              <p>
                <b>Fecha fin:</b> {contrato.fechaFin}
              </p>
            )}

            {contrato.estado === "ACTIVO" && (
              <button
                onClick={onCancelarContrato}
                disabled={contratoSaving}
                className="bg-red-600 text-white px-3 py-2 rounded disabled:opacity-60"
              >
                {contratoSaving ? "Cancelando..." : "Cancelar contrato"}
              </button>
            )}
          </div>
        ) : (
          <div className="space-y-3">
            <p className="text-gray-700">
              No tienes contrato activo. Selecciona un comisionista para registrarlo.
            </p>

            <div className="grid gap-3 md:grid-cols-2">
              <div>
                <label className="block text-sm mb-1">Comisionista</label>
                <select
                  className="border p-2 rounded w-full"
                  value={selectedComisionistaId}
                  onChange={(e) => setSelectedComisionistaId(e.target.value)}
                >
                  <option value="">-- Selecciona --</option>
                  {comisionistas.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.nombre} {c.apellido} — {c.correo}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm mb-1">Moneda</label>
                <select
                  className="border p-2 rounded w-full"
                  value={moneda}
                  onChange={(e) => setMoneda(e.target.value)}
                >
                  <option value="COP">COP</option>
                  <option value="VES">VES</option>
                  <option value="USD">USD</option>
                  <option value="PEN">PEN</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-sm mb-1">Observaciones (opcional)</label>
              <textarea
                className="border p-2 rounded w-full"
                value={observaciones}
                onChange={(e) => setObservaciones(e.target.value)}
                rows={3}
                placeholder="Ej: Quiero trabajar con este comisionista."
              />
            </div>

            <div className="flex items-center gap-2">
              <button
                type="button"
                className="px-3 py-2 border rounded"
                onClick={() => setShowTerms(true)}
              >
                Ver términos
              </button>
              <label className="flex items-center gap-2 text-sm">
                <input
                  type="checkbox"
                  checked={acceptedTerms}
                  onChange={(e) => setAcceptedTerms(e.target.checked)}
                />
                Acepto los términos
              </label>
            </div>

            <button
              onClick={onRegistrarContrato}
              disabled={!selectedComisionistaId || !acceptedTerms || contratoSaving}
              className="bg-green-600 text-white px-3 py-2 rounded disabled:opacity-60"
            >
              {contratoSaving ? "Registrando..." : "Registrar contrato"}
            </button>
          </div>
        )}
      </section>

      {/* Modal de Términos */}
      <Modal
        open={showTerms}
        title="Términos y Condiciones del Contrato"
        onClose={() => setShowTerms(false)}
        footer={
          <div className="flex justify-end gap-2">
            <button
              className="px-3 py-2 border rounded"
              onClick={() => setShowTerms(false)}
            >
              Cerrar
            </button>
            <button
              className="px-3 py-2 bg-green-600 text-white rounded"
              onClick={() => {
                setAcceptedTerms(true);
                setShowTerms(false);
              }}
            >
              Aceptar
            </button>
          </div>
        }
      >
        <pre className="whitespace-pre-wrap text-sm leading-6">
          {buildTerminos()}
        </pre>
      </Modal>
    </div>
  );
}
