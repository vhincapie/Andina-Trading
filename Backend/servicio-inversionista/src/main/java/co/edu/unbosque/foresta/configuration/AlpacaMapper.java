package co.edu.unbosque.foresta.configuration;

import co.edu.unbosque.foresta.model.DTO.*;
import co.edu.unbosque.foresta.utils.AsciiSanitizer;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

public class AlpacaMapper {

    public static CreateAccountRequestDTO fromInversionista(InversionistaRegistroRequestDTO src, String ip) {
        CreateAccountRequestDTO req = new CreateAccountRequestDTO();

        String iso3 = resolveIso3(src.getCountryIso3(), src.getCountryIso2(), "COL");

        ContactDTO contact = new ContactDTO();
        contact.setEmailAddress(src.getCorreo());
        contact.setPhoneNumber((empty(src.getPhoneNumber())) ? "+573001112233" : src.getPhoneNumber());
        contact.setStreetAddress(AsciiSanitizer.asciiPrintable(
                empty(src.getStreetAddress()) ? "Calle 123 #45-67" : src.getStreetAddress()
        ));
        contact.setPostalCode(empty(src.getPostalCode()) ? "110111" : src.getPostalCode());
        contact.setCity(AsciiSanitizer.asciiPrintable(empty(src.getCity()) ? "Bogota" : src.getCity()));
        contact.setState(AsciiSanitizer.asciiPrintable(empty(src.getState()) ? "Cundinamarca" : src.getState()));
        contact.setCountry(iso3);
        req.setContact(contact);

        IdentityDTO identity = new IdentityDTO();
        identity.setGivenName(AsciiSanitizer.asciiPrintable(src.getNombre()));
        identity.setFamilyName(AsciiSanitizer.asciiPrintable(src.getApellido()));
        identity.setDateOfBirth(src.getFechaNacimiento().toString());
        identity.setTaxId(AsciiSanitizer.asciiPrintable(src.getNumeroDocumento()));
        identity.setTaxIdType(mapTaxIdType(src.getTipoDocumento()));
        identity.setCountryOfCitizenship(iso3);
        identity.setCountryOfBirth(iso3);
        identity.setCountryOfTaxResidence(iso3);
        req.setIdentity(identity);

        DisclosuresDTO disc = new DisclosuresDTO();
        disc.setControlPerson(false);
        disc.setAffiliatedExchangeOrFinra(false);
        disc.setAffiliatedExchangeOrIiroc(false);
        disc.setPoliticallyExposed(false);
        disc.setImmediateFamilyExposed(false);
        disc.setDiscretionary(false);
        req.setDisclosures(disc);

        AgreementDTO ag = new AgreementDTO(
                "customer_agreement",
                Instant.now().toString(),
                (ip == null || ip.isBlank()) ? "127.0.0.1" : ip
        );
        req.setAgreements(List.of(ag));

        req.setEnabledAssets(List.of("us_equity"));
        req.setAdditionalInformation("Registro automático desde Foresta (sandbox)");
        req.setTrustedContact(null);

        return req;
    }

    private static boolean empty(String s){ return s == null || s.isBlank(); }

    private static String mapTaxIdType(String tipo) {
        if (tipo == null) return "other";
        switch (tipo.trim().toUpperCase()) {
            case "CC":
            case "DNI": return "national_id";
            case "CE":
            case "PASAPORTE": return "passport";
            default: return "other";
        }
    }

    private static String toISO3(String code) {
        if (code == null) return null;
        String c = code.trim().toUpperCase();
        if (c.length() == 3) return c;
        if (c.length() == 2) {
            try { return new Locale("", c).getISO3Country().toUpperCase(); }
            catch (MissingResourceException e) { throw new IllegalArgumentException("Código de país inválido: " + code); }
        }
        throw new IllegalArgumentException("Código de país inválido (esperado ISO2 o ISO3): " + code);
    }

    private static String resolveIso3(String iso3, String iso2, String fallbackIso3) {
        if (!empty(iso3)) return iso3.trim().toUpperCase();
        if (!empty(iso2)) return toISO3(iso2);
        return fallbackIso3;
    }
}
