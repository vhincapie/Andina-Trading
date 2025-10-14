import { useEffect, useState } from "react";
import {
  registrarContrato,
  getMiContratoActivo,
  cancelarMiContratoActivo,
} from "../../api/contratoService";
import { listarComisionistas } from "../../api/comisionistaService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";
import Modal from "../../components/Modal";

export default function ContratosInversionistaPage() {
  const [contrato, setContrato] = useState(null);
  const [comisionistas, setComisionistas] = useState([]);
  const [selectedComisionistaId, setSelectedComisionistaId] = useState("");
  const [moneda, setMoneda] = useState("COP");
  const [observaciones, setObservaciones] = useState("");
  const [contratoLoading, setContratoLoading] = useState(true);
  const [contratoSaving, setContratoSaving] = useState(false);

  const [showTerms, setShowTerms] = useState(false);
  const [acceptedTerms, setAcceptedTerms] = useState(false);

  const [ok, setOk] = useState("");
  const [error, setError] = useState("");

  const porcentajeDefault = Number(
    import.meta.env.VITE_CONTRATOS_PORCENTAJE_DEFAULT ?? 2.5
  );

  const reloadContrato = async () => {
    setContratoLoading(true);
    try {
      const c = await getMiContratoActivo().catch(() => null);
      setContrato(c);
    } finally {
      setContratoLoading(false);
    }
  };

  useEffect(() => {
    if (!error && !ok) return;
    const t = setTimeout(() => {
      setError("");
      setOk("");
    }, 6000);
    return () => clearTimeout(t);
  }, [error, ok]);

  useEffect(() => {
    (async () => {
      try {
        const lista = await listarComisionistas().catch(() => []);
        setComisionistas(lista || []);
      } catch (e) {
        console.error(e);
        setError("No fue posible cargar la lista de comisionistas.");
      }
      await reloadContrato();
    })();
  }, []);

  const buildTerminos = () => {
    const raw = `
ANDINA TRADING S.A.S. – TÉRMINOS Y CONDICIONES DEL CONTRATO DE COMISIONAMIENTO

1. Partes.
Intervienen: (i) ANDINA TRADING S.A.S. (“Andina Trading”), en calidad de comisionista, a través del comisionista seleccionado por el inversionista, y (ii) el Inversionista identificado en la orden de registro realizada en la plataforma.

2. Objeto.
El Comisionista realizará, por cuenta y riesgo del Inversionista, actividades de intermediación y ejecución de órdenes de inversión conforme a las instrucciones del Inversionista y a las políticas internas de Andina Trading.

3. Honorarios.
El Inversionista reconoce y acepta un honorario estándar equivalente al {{PORCENTAJE}}% sobre el valor administrado y/o resultados según aplique, cobrado con la periodicidad definida por Andina Trading.

4. Moneda y cargos.
La moneda del contrato será {{MONEDA}}. El Inversionista asume costos, impuestos, comisiones bancarias, cambiarias y demás cargos aplicables por terceros.

12. Terminación.
Cualquiera de las partes podrá terminar el contrato en cualquier momento con aviso por los canales oficiales.

15. Aceptación.
Al marcar “Acepto los términos” y registrar la orden en la plataforma, el Inversionista declara haber leído y aceptado íntegramente este contrato.
    `
      .replaceAll("{{PORCENTAJE}}", String(porcentajeDefault))
      .replaceAll("{{MONEDA}}", moneda);

    return raw.trim();
  };

  const onRegistrarContrato = async () => {
    setError("");
    setOk("");
    if (!selectedComisionistaId)
      return setError("Debes seleccionar un comisionista.");
    if (!acceptedTerms)
      return setError("Debes aceptar los términos del contrato.");
    setContratoSaving(true);
    try {
      const payload = {
        comisionistaId: Number(selectedComisionistaId),
        moneda,
        observaciones: (observaciones || "").trim(),
        aceptaTerminos: true,
      };
      await registrarContrato(payload);
      await reloadContrato(); 
      setOk("Contrato registrado con éxito.");
      setSelectedComisionistaId("");
      setObservaciones("");
      setAcceptedTerms(false);
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
      await cancelarMiContratoActivo();
      await reloadContrato(); 
      setOk("Contrato cancelado.");
      if (contrato && contrato.estado !== "ACTIVO") {
        setContrato(null);
      }
    } catch (e) {
      setError(
        e?.response?.data?.message || "No fue posible cancelar el contrato."
      );
    } finally {
      setContratoSaving(false);
    }
  };

  const renderFormRegistro = () => (
    <div className="bg-white border rounded p-4 space-y-3">
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
                {c.nombre} {c.apellido} — {c.aniosExperiencia} año
                {c.aniosExperiencia !== 1 ? "s" : ""}
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
  );

  return (
    <div className="max-w-3xl mx-auto px-4 py-6 space-y-3">
      <h2 className="text-2xl font-semibold">Contrato</h2>

      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      {contratoLoading ? (
        <p>Cargando contrato...</p>
      ) : contrato && contrato.estado === "ACTIVO" ? (
        <div className="bg-white border rounded p-4 space-y-1">
          <p>
            <b>Comisionista:</b> {contrato.comisionistaNombreCompleto || "—"}
          </p>
          <p>
            <b>Inversionista:</b> {contrato.inversionistaNombreCompleto || "—"}{" "}
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
            <b>Porcentaje cobro:</b> {contrato.porcentajeCobroAplicado}%
          </p>
          <p>
            <b>Fecha inicio:</b> {contrato.fechaInicio || "—"}
          </p>
          {!!contrato.fechaFin && (
            <p>
              <b>Fecha fin:</b> {contrato.fechaFin}
            </p>
          )}

          <button
            onClick={onCancelarContrato}
            disabled={contratoSaving}
            className="bg-red-600 text-white px-3 py-2 rounded disabled:opacity-60"
          >
            {contratoSaving ? "Cancelando..." : "Cancelar contrato"}
          </button>
        </div>
      ) : (
        renderFormRegistro()
      )}

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
