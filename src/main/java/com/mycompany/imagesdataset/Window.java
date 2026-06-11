package com.mycompany.imagesdataset;

import com.mycompany.exceptions.InvalidDestinationFolderException;
import com.mycompany.exceptions.InvalidFileNameException;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Window extends JFrame {

	private static final long serialVersionUID = 1669389751101095407L;
	private static final Logger LOG = LoggerFactory.getLogger(Window.class);
	private static final String NO_FILTER_SELECTION = "Ninguno";

	public Window() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(200, 20, 700, 400);
		Container contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		setElementsInContentPane(contentPane, this);
	}

	private static void setElementsInContentPane(Container contentPane, Window window) {

		initializeFolderPathDescriptionLabel(contentPane);
		initializeFilePathDescriptionLabel(contentPane);
		JLabel filtersDescription = initializeFiltersDescriptionLabel(contentPane);

		JLabel folderPathGuideLabel = initializeFolderPathGuideLabel(contentPane);
		initializeChooseButton(contentPane, folderPathGuideLabel, JFileChooser.DIRECTORIES_ONLY);
		JLabel filePathGuideLabel = initializeFilePathGuideLabel(contentPane);
		initializeChooseButton(contentPane, filePathGuideLabel, JFileChooser.FILES_ONLY);
		JTextField imageAmountLimitField = initializeField(contentPane, "amount");
		JTextField operatorIdField = initializeField(contentPane, "operator");
		initializeImageAmountLimitLabel(contentPane);
		initializeOperatorIdLabel(contentPane);
		initializeInfoLabel(contentPane);

		JLabel sizeFilterLabel = new JLabel();
		JLabel imageTypeFilterLabel = new JLabel();
		JLabel imageFormatFilterLabel = new JLabel();
		JLabel licencesFilterLabel = new JLabel();
		Container filtersPanel = new JPanel();
		Map<String, JLabel> searchFilters = initializeFiltersPanel(contentPane, filtersPanel, sizeFilterLabel,
				imageTypeFilterLabel, imageFormatFilterLabel, licencesFilterLabel);

		JButton btnIni = initializeIniButton(contentPane, folderPathGuideLabel::getText, filePathGuideLabel::getText,
				imageAmountLimitField::getText, searchFilters, operatorIdField::getText);
		initializeFiltersButton(window, contentPane, filtersPanel, btnIni, filtersDescription);
	}

	private static void initializeOperatorIdLabel(Container contentPane) {
		JLabel lblImageAmountLimit = new JLabel("Iniciales de la persona que corre este programa (Por ej: MG)");
		addComponent(contentPane, lblImageAmountLimit, 12, 228, 432, 19);
	}

	private static void run(String destinationFolderPath, String namesFilePath, String imageAmountLimit,
			String operatorId,
			Map<String, JLabel> searchFilters,
			Container contentPane) {
		try {
			checkFolder(destinationFolderPath);
			checkNamesFile(namesFilePath);
		} catch (InvalidDestinationFolderException | InvalidFileNameException | IOException e) {
			LOG.error("Error with folder or file path", e);
			JOptionPane.showMessageDialog(contentPane, "Las rutas no fueron correctamente indicadas");
		}

		ImageManager.downloadImages(destinationFolderPath, namesFilePath, Integer.parseInt(imageAmountLimit),
				operatorId,
				getFilterList(searchFilters));
		JOptionPane.showMessageDialog(contentPane, "Finalizado");
		LOG.info("Finalizado");
	}

	private static Map<String, JLabel> initializeFiltersPanel(Container contentPane, Container filtersPanel,
			JLabel resolutionFilterLabel, JLabel imageTypeFilterLabel, JLabel imageFormatFilterLabel,
			JLabel licencesFilterLabel) {

		filtersPanel.setBounds(12, 350, 600, 300);
		contentPane.add(filtersPanel);
		filtersPanel.setVisible(false);
		Map<String, JLabel> searchFilters = new HashMap<>();
		searchFilters.put("resolution", resolutionFilterLabel);
		searchFilters.put("type", imageTypeFilterLabel);
		searchFilters.put("format", imageFormatFilterLabel);
		searchFilters.put("licences", licencesFilterLabel);
		initializeFilterLabelsAndButtons(filtersPanel, resolutionFilterLabel, imageTypeFilterLabel,
				imageFormatFilterLabel,
				licencesFilterLabel);

		return searchFilters;
	}

	private static void initializeFiltersButton(Window window, Container contentPane, Container filtersPanel,
			JButton btnIni, JLabel filtersDescription) {
		JButton btnFilters = new JButton("Ver filtros");
		addButton(contentPane, btnFilters, 14, 280, 147, 25, e -> {
			if (filtersPanel.isVisible()) {
				filtersPanel.setVisible(false);
				filtersDescription.setVisible(false);
				window.setBounds(200, 20, 700, 400);
				btnIni.setBounds(14, 320, 117, 25);
				btnFilters.setText("Ver filtros");
			} else {
				filtersPanel.setVisible(true);
				filtersDescription.setVisible(true);
				window.setBounds(200, 20, 700, 750);
				btnIni.setBounds(14, 670, 117, 25);
				btnFilters.setText("Ocultar filtros");
			}
		});
	}

	private static void initializeFilterLabelsAndButtons(Container contentPane, JLabel sizeFilterLabel,
			JLabel imageTypeFilterLabel, JLabel imageFormatFilterLabel, JLabel licencesFilterLabel) {
		Container filtersPanel1 = new JPanel();
		Container filtersPanel2 = new JPanel();
		Container filtersPanel3 = new JPanel();
		Container filtersPanel4 = new JPanel();
		addPanel(contentPane, filtersPanel1, 70, 350, 50, 400);
		addPanel(contentPane, filtersPanel2, 70, 350, 50, 400);
		addPanel(contentPane, filtersPanel3, 70, 350, 50, 400);
		addPanel(contentPane, filtersPanel4, 70, 350, 50, 400);

		initializeSizeFilterLabelButton(filtersPanel1, sizeFilterLabel);
		initializeImageTypeFilterLabelButton(filtersPanel2, imageTypeFilterLabel);
		initializeImageFormatFilterLabelButton(filtersPanel3, imageFormatFilterLabel);
		initializeLicencesFilterLabelButton(filtersPanel4, licencesFilterLabel);
	}

	private static void initializeFolderPathDescriptionLabel(Container contentPane) {
		JLabel lblFolderPath = new JLabel("Ruta donde se guardarán las carpetas con imágenes");
		addComponent(contentPane, lblFolderPath, 12, -94, 440, 231);
	}

	private static void initializeChooseButton(Container contentPane, JLabel pathGuideLabel, int selectionMode) {
		JButton btnChooseFile = new JButton("Seleccionar...");
		int yPoint = selectionMode == JFileChooser.DIRECTORIES_ONLY ? 41 : 112;
		addButton(contentPane, btnChooseFile, 12, yPoint, 186, 25, e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(selectionMode);
			fileChooser.showOpenDialog(contentPane);
			File names = fileChooser.getSelectedFile();
			if ((names == null) || (names.getName().isEmpty())) {
				JOptionPane.showMessageDialog(contentPane, "Nombre de carpeta o archivo inválido");
			} else {
				pathGuideLabel.setText(names.getAbsolutePath());
			}
		});
	}

	private static JButton initializeIniButton(Container contentPane, Supplier<String> folderPathGuideLabel,
			Supplier<String> filePathGuideLabel, Supplier<String> imageAmountLimitLabel,
			Map<String, JLabel> searchFilters,
			Supplier<String> operatorIdLabel) {
		JButton btnIni = new JButton("Iniciar");
		addButton(contentPane, btnIni, 14, 320, 117, 25, e -> {
			JOptionPane.showMessageDialog(contentPane,
					"Se iniciara el proceso. Se informará cuando se haya finalizado. Presione OK para confirmar");
			run(folderPathGuideLabel.get(), filePathGuideLabel.get(), imageAmountLimitLabel.get(), operatorIdLabel
					.get(), searchFilters,
					contentPane);
		});
		return btnIni;
	}

	private static void initializeInfoLabel(Container contentPane) {
		JTextArea savingInfo = new JTextArea(
				"(El limite de imágenes por defecto es de 1000,\n puede cambiar este número)");
		addComponent(contentPane, savingInfo, 22, 180, 517, 52);
		savingInfo.setOpaque(false);
	}

	private static JLabel initializeFolderPathGuideLabel(Container contentPane) {
		JLabel lblLabelDirectoryPath = new JLabel();
		addComponent(contentPane, lblLabelDirectoryPath, 228, 45, 400, 15);
		return lblLabelDirectoryPath;
	}

	private static JLabel initializeFilePathGuideLabel(Container contentPane) {
		JLabel lblLabelFilePath = new JLabel();
		addComponent(contentPane, lblLabelFilePath, 228, 117, 400, 15);
		return lblLabelFilePath;
	}

	private static void initializeFilePathDescriptionLabel(Container contentPane) {
		JLabel lblFilePath = new JLabel("Ruta del archivo con nombres de personas");
		addComponent(contentPane, lblFilePath, 12, 81, 392, 19);
	}

	private static void initializeImageAmountLimitLabel(Container contentPane) {
		JLabel lblImageAmountLimit = new JLabel("Limite para la cantidad de imágenes a descargar");
		addComponent(contentPane, lblImageAmountLimit, 12, 148, 392, 19);
	}

	private static JTextField initializeField(Container contentPane, String description) {
		JTextField field = new JTextField();
		if (description.equals("amount")) {
			field.setText("5000");
			addComponent(contentPane, field, 372, 148, 57, 23);
		} else {
			field.setText("");
			addComponent(contentPane, field, 457, 228, 57, 23);
		}
		return field;
	}

	private static JLabel initializeFiltersDescriptionLabel(Container contentPane) {
		JLabel lblFilePath = new JLabel("Seleccione los filtros que desee para cada categoría");
		addComponent(contentPane, lblFilePath, 12, 327, 392, 19);
		lblFilePath.setVisible(false);
		return lblFilePath;
	}

	private static void initializeSizeFilterLabelButton(Container contentPane, JLabel sizeFilterLabel) {
		JLabel imageResolutionLbl = new JLabel("Resolución:", SwingConstants.CENTER);
		addComponent(contentPane, imageResolutionLbl, 798, 2885, 100, 20);

		JRadioButton noFilterSelection = new JRadioButton(NO_FILTER_SELECTION, true);
		List<JRadioButton> buttons = Stream.of("400x300", "640x480", "800x600", "1024x768", "2mp", "4mp", "6mp", "8mp",
				"10mp", "12mp", "15mp", "20mp", "40mp", "70mp")
				.map(resolution -> new JRadioButton(resolution, false))
				.collect(Collectors.toList());

		ButtonGroup sizeOptionsGroup = new ButtonGroup();
		sizeOptionsGroup.add(noFilterSelection);
		buttons.forEach(sizeOptionsGroup::add);

		addButton(contentPane, noFilterSelection, 12, 301, 100, 20, e -> sizeFilterLabel.setText(""));
		int y = 321;
		for (JRadioButton button : buttons) {
			addButton(contentPane, button, 12, y, 100, 20, e -> sizeFilterLabel.setText(button.getText()));
			y += 20;
		}
	}

	private static void initializeImageTypeFilterLabelButton(Container contentPane, JLabel sizeFilterLabel) {
		JLabel imageTypeLbl = new JLabel("        Tipo:");
		addComponent(contentPane, imageTypeLbl, 712, 278, 100, 20);

		JRadioButton noFilterSelection = new JRadioButton(NO_FILTER_SELECTION, true);
		List<JRadioButton> buttons = Stream.of("face", "photo", "clipart", "lineart", "animated")
				.map(resolution -> new JRadioButton(resolution, false))
				.collect(Collectors.toList());
		ButtonGroup sizeOptionsGroup = new ButtonGroup();
		sizeOptionsGroup.add(noFilterSelection);
		buttons.forEach(sizeOptionsGroup::add);

		addButton(contentPane, noFilterSelection, 702, 301, 200, 20, e -> sizeFilterLabel.setText(""));
		int y = 321;
		for (JRadioButton button : buttons) {
			addButton(contentPane, button, 702, y, 200, 20, e -> sizeFilterLabel.setText(button.getText()));
			y += 20;
		}

	}

	private static void initializeImageFormatFilterLabelButton(Container contentPane, JLabel sizeFilterLabel) {
		JLabel formatLbl = new JLabel("Formato:", SwingConstants.CENTER);
		addComponent(contentPane, formatLbl, 512, 278, 100, 20);

		JRadioButton noFilterSelection = new JRadioButton(NO_FILTER_SELECTION, true);
		List<JRadioButton> buttons = Stream.of("jpg", "png", "bmp", "webp")
				.map(resolution -> new JRadioButton(resolution, false))
				.collect(Collectors.toList());

		ButtonGroup sizeOptionsGroup = new ButtonGroup();
		sizeOptionsGroup.add(noFilterSelection);
		buttons.forEach(sizeOptionsGroup::add);

		addButton(contentPane, noFilterSelection, 502, 301, 200, 20, e -> sizeFilterLabel.setText(""));
		int y = 321;
		for (JRadioButton button : buttons) {
			addButton(contentPane, button, 502, y, 200, 20, e -> sizeFilterLabel.setText(button.getText()));
			y += 20;
		}
	}

	private static void initializeLicencesFilterLabelButton(Container contentPane, JLabel sizeFilterLabel) {
		JLabel licencesLbl = new JLabel("Licencias:", SwingConstants.CENTER);
		addComponent(contentPane, licencesLbl, 412, 278, 100, 20);

		JRadioButton noFilterSelection = new JRadioButton(NO_FILTER_SELECTION, true);
		List<JRadioButton> buttons = Stream.of("Licencias creative commons", "Licencias comerciales y otras")
				.map(resolution -> new JRadioButton(resolution, false))
				.collect(Collectors.toList());

		ButtonGroup sizeOptionsGroup = new ButtonGroup();
		sizeOptionsGroup.add(noFilterSelection);
		buttons.forEach(sizeOptionsGroup::add);

		addButton(contentPane, noFilterSelection, 202, 301, 200, 20, e -> sizeFilterLabel.setText(""));
		int y = 321;
		for (JRadioButton button : buttons) {
			addButton(contentPane, button, 202, y, 200, 20, e -> sizeFilterLabel.setText(button.getText()));
			y += 20;
		}
	}

	private static void checkFolder(String folder) throws InvalidDestinationFolderException {
		File toCheck = new File(folder);
		boolean valid = toCheck.exists() && toCheck.isDirectory();

		if (!valid)
			throw new InvalidDestinationFolderException("Destination folder path is not valid");
	}

	private static void checkNamesFile(String path) throws InvalidFileNameException, IOException {
		File toCheck = new File(path);
		boolean emptyFile = FileUtils.readFileToString(toCheck, Charset.defaultCharset()).isEmpty();

		if (path == null || path.isEmpty() || !toCheck.exists() || !toCheck.isFile() || emptyFile)
			throw new InvalidFileNameException("Invalid names file");
	}

	private static void addComponent(Container contentPane, JComponent component, int x, int y, int width, int height) {
		component.setBounds(x, y, width, height);
		contentPane.add(component);
	}

	private static void addPanel(Container contentPane, Container panel, int x, int y, int width, int height) {
		panel.setBounds(x, y, width, height);
		panel.setLayout(new GridLayout(0, 1));
		((JComponent) panel).setBorder(new EmptyBorder(1, 1, 10, 0));
		contentPane.add(panel);
	}

	private static void addButton(Container contentPane, AbstractButton button, int x, int y, int width, int height,
			ActionListener action) {
		addComponent(contentPane, button, x, y, width, height);
		button.addActionListener(action);
	}

	private static List<String> getFilterList(Map<String, JLabel> filtersList) {

		Map<String, String> stringsMap = filtersList.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getText()));
		return SearchFilters.getFilters(stringsMap);
	}

}
