package expedientesx.util;

import java.util.Calendar;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

public class HorarioVoter implements AuthorizationManager<RequestAuthorizationContext> {

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		int minutoActual = Calendar.getInstance().get(Calendar.MINUTE);
		boolean concedido = minutoActual % 2 == 0;

		System.out.println("=======================================================");
		System.out.println("=======================================================");
		System.out.println("=======================================================");
		System.out.println("=======================================================");
		System.out.println("=======================================================");
		System.out.println("=======================================================");
		System.out.println("Votando: " + concedido + " para el minuto " + minutoActual);

		return new AuthorizationDecision(concedido);
	}

}
