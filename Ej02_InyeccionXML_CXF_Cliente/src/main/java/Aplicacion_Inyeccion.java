import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Aplicacion_Inyeccion extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField jtfLogin;
	private JTextField jtfPassword;
	private JTextField jtfRol;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Aplicacion_Inyeccion frame = new Aplicacion_Inyeccion();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Aplicacion_Inyeccion() {
		
		setTitle("Swing del bueno");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
				
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JButton btnModificar = new JButton("Modificar");
		btnModificar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				modificarUsuario();
			}
		});
		panel.add(btnModificar);
		
		JButton btnInyeccion = new JButton("Inyección");
		btnInyeccion.setBackground(Color.red);
		btnInyeccion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtfRol.setText("1</rol>"+
				        "<id>123456</id>"+
				        "<rol>1");
			}
		});
		
		/*
		<?xml version="1.0" encoding="UTF-8"?>
		<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<soapenv:Body>
				<insertarUsuario xmlns="http://servicio.modelo.curso.com">
					<usuarioDTO>
						<ns1:id xmlns:ns1="http://entidad.modelo.curso.com">1</ns1:id>
						<ns2:login xmlns:ns2="http://entidad.modelo.curso.com">2</ns2:login>
						<ns3:pw xmlns:ns3="http://entidad.modelo.curso.com">3</ns3:pw>
						<ns4:rol xmlns:ns4="http://entidad.modelo.curso.com">1&lt;/ns4:rol&gt;&lt;ns1:id xmlns:ns1=&quot;http://entidad.modelo.curso.com&quot;&gt;123456&lt;/ns1:id&gt;&lt;ns4:rol xmlns:ns4=&quot;http://entidad.modelo.curso.com&quot;&gt;1</ns4:rol>
					</usuarioDTO>
				</insertarUsuario>
			</soapenv:Body>
		</soapenv:Envelope>
		*/
		
		panel.add(btnInyeccion);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblId = new JLabel("Id");
		lblId.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.insets = new Insets(0, 0, 5, 5);
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 0;
		panel_1.add(lblId, gbc_lblId);
		
		JLabel lblIdValor = new JLabel("1");
		lblIdValor.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblIdValor = new GridBagConstraints();
		gbc_lblIdValor.anchor = GridBagConstraints.WEST;
		gbc_lblIdValor.insets = new Insets(0, 0, 5, 0);
		gbc_lblIdValor.gridx = 1;
		gbc_lblIdValor.gridy = 0;
		panel_1.add(lblIdValor, gbc_lblIdValor);
		
		JLabel lblNombre = new JLabel("Login");
		lblNombre.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblNombre = new GridBagConstraints();
		gbc_lblNombre.anchor = GridBagConstraints.EAST;
		gbc_lblNombre.insets = new Insets(0, 0, 5, 5);
		gbc_lblNombre.gridx = 0;
		gbc_lblNombre.gridy = 1;
		panel_1.add(lblNombre, gbc_lblNombre);
		
		jtfLogin = new JTextField();
		GridBagConstraints gbc_jtfLogin = new GridBagConstraints();
		gbc_jtfLogin.insets = new Insets(0, 0, 5, 0);
		gbc_jtfLogin.fill = GridBagConstraints.HORIZONTAL;
		gbc_jtfLogin.gridx = 1;
		gbc_jtfLogin.gridy = 1;
		panel_1.add(jtfLogin, gbc_jtfLogin);
		jtfLogin.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.anchor = GridBagConstraints.EAST;
		gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 2;
		panel_1.add(lblPassword, gbc_lblPassword);
		
		jtfPassword = new JTextField();
		GridBagConstraints gbc_jtfPassword = new GridBagConstraints();
		gbc_jtfPassword.anchor = GridBagConstraints.NORTH;
		gbc_jtfPassword.insets = new Insets(0, 0, 5, 0);
		gbc_jtfPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_jtfPassword.gridx = 1;
		gbc_jtfPassword.gridy = 2;
		panel_1.add(jtfPassword, gbc_jtfPassword);
		jtfPassword.setColumns(10);
		
		JLabel lblRol = new JLabel("Rol");
		lblRol.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblRol = new GridBagConstraints();
		gbc_lblRol.anchor = GridBagConstraints.EAST;
		gbc_lblRol.insets = new Insets(0, 0, 5, 5);
		gbc_lblRol.gridx = 0;
		gbc_lblRol.gridy = 3;
		panel_1.add(lblRol, gbc_lblRol);
		
		jtfRol = new JTextField();
		GridBagConstraints gbc_jtfRol = new GridBagConstraints();
		gbc_jtfRol.insets = new Insets(0, 0, 5, 0);
		gbc_jtfRol.fill = GridBagConstraints.HORIZONTAL;
		gbc_jtfRol.gridx = 1;
		gbc_jtfRol.gridy = 3;
		panel_1.add(jtfRol, gbc_jtfRol);
		jtfRol.setColumns(10);
		
		JTextArea txtArea = new JTextArea();
		GridBagConstraints gbc_txtArea = new GridBagConstraints();
		gbc_txtArea.anchor = GridBagConstraints.NORTH;
		gbc_txtArea.gridwidth = 2;
		gbc_txtArea.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtArea.gridx = 0;
		gbc_txtArea.gridy = 4;
		txtArea.setRows(5);
		txtArea.setText("1</rol>"+
				        "<id>123456</id>"+
				        "<rol>1");		
		panel_1.add(txtArea, gbc_txtArea);
	}

	private void modificarUsuario(){
		
		String id    = "1";
		String login = jtfLogin.getText();
		String pw    = jtfPassword.getText();
		String rol   = jtfRol.getText();
		
		String url = "http://localhost:8080/Ej02_InyeccionXML_CXF/services/ServicioUsuariosPort";
		
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
			//Configuración de la cabecera de la petición
			con.setRequestMethod("POST");
			
			con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			con.setRequestProperty("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
			con.setRequestProperty("User-Agent", "Axis/1.4");
			con.setRequestProperty("Host", "localhost:8081");
			con.setRequestProperty("Cache-Control", "no-cache");
			con.setRequestProperty("Pragma", "no-cache");
			con.setRequestProperty("SOAPAction", "\"\"");
			con.setRequestProperty("Content-Length", "613");		
			
			String urlParameters = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
					"<soapenv:Body>"+
						"<modificarUsuario xmlns=\"http://servicio.modelo.curso.com/\">"+
							"<arg0 xmlns=\"\">"+
								"<id>"+id+"</id>"+
								"<login>"+login+"</login>"+
								"<pw>"+pw+"</pw>"+
								"<rol>"+rol+"</rol>"+
							"</arg0>"+
						"</modificarUsuario>"+
					"</soapenv:Body>"+
				"</soapenv:Envelope>";		

			//Enviamos la petición
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
 
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
