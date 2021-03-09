
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

package source;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import source.utility.ListModelObject;
import source.utility.Misc;
import source.utility.SimpleFrame;
import source.utility.json.JSONReader;

//TODO: Optimize nested loops

/**
 * @author Nathin Wascher
 */
public class PS_Panel implements ListSelectionListener, DocumentListener, ItemListener {
    private static final SimpleFrame FRAME = new SimpleFrame("PokemonSearch", "Guess That Pokemon!", 1000, 600, false);

    private static final PS_DownloadPokeJSONs PS_DPJ = new PS_DownloadPokeJSONs();
    private static final PS_Searcher POKE_SEARCH = new PS_Searcher();
    private static final JPanel MENU_PANEL = new JPanel();

    private static final String[] EVO_LIST = { "Only", "First", "Second", "Final" };
    private static final String BORDER = "|}>----------<{::}>----------<{::}>----------<{|";

    private static final int SEARCH_BAR_WIDTH = 12;
    private static final int TYPE_PANE_WIDTH = 65;
    private static final int TYPE_PANE_HEIGHT = 145;

    private static final int REGION_PANE_WIDTH = 100;
    private static final int REGION_PANE_HEIGHT = 95;

    private JSONReader jsonReader;

    private JTextField searchTF;

    private JCheckBox checkBox1;
    private JCheckBox checkBox2;

    private JList<ListModelObject> typeJL;
    private JList<ListModelObject> regionJL;
    private JList<ListModelObject> evolutionJL;

    private ArrayList<String> typeInput;
    private ArrayList<String> regionInput;
    private ArrayList<String> evolutionInput;
    private ArrayList<String> typeList;
    private ArrayList<String> regionList;

    /**
     * 
     */
    public PS_Panel() {
        ArrayList<String> message = initialize();

        MENU_PANEL.setLayout(new GridBagLayout());
        MENU_PANEL.setBackground(Color.RED);

        PS_DPJ.readPokeInfo();
        createTypeList();
        createRegionList();
        setUpPanels();

        Misc.showInfoBox(MENU_PANEL, "Pokemon Search v2.1.1", message);

        FRAME.setTitleImage(Misc.findImageIcon());
        FRAME.setFullscreen(true);
        FRAME.setLayout(new GridLayout(1, 2));
        FRAME.add(POKE_SEARCH.getOutputListPane());
        FRAME.add(MENU_PANEL);
        FRAME.setVisible(true);
    }

    private ArrayList<String> initialize() {
        ArrayList<String> message = new ArrayList<String>();

        message.add("Copyright (c) 2020 Nathin-Dolphin");
        message.add("\nThis file is under the MIT License");
        message.add("\nPokemon is a registered trademark of Nintendo");
        message.add("\n\nDescription:\nSearch for Pokemon by name, number, type, and evolution.");
        message.add("\n\n[!] Known Issues:");
        message.add("\n- The screen fails to load all of the pokemon when the window appears.");
        message.add("\n- Does not search for names with numbers (Ex. Porygon2) using numbers.");
        message.add("\n- The windows showing the pokemon's information does not exit upon the exit of the main window.");

        regionInput = new ArrayList<String>();
        typeInput = new ArrayList<String>();
        evolutionInput = new ArrayList<String>();

        jsonReader = new JSONReader();

        PS_DPJ.setJsonReader(jsonReader);
        POKE_SEARCH.setJsonReader(jsonReader);

        return message;
    }

    //
    private void createTypeList() {
        DefaultListModel<ListModelObject> tempDLM = new DefaultListModel<>();
        ListModelObject tempLMO;

        typeList = jsonReader.get("types");
        for (int i = 0; i < typeList.size(); i++) {
            tempLMO = new ListModelObject(typeList.get(i));
            tempDLM.addElement(tempLMO);
        }
        typeJL = new JList<>(tempDLM);
        typeJL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        typeJL.addListSelectionListener(this);

        POKE_SEARCH.setTypeInput(typeList);
    }

    //
    private void createRegionList() {
        DefaultListModel<ListModelObject> tempDLM = new DefaultListModel<>();
        ListModelObject tempLMO;

        regionList = jsonReader.get("regions");
        tempDLM = new DefaultListModel<>();
        for (int i = 0; i < regionList.size(); i = i + 2) {
            regionInput.add(regionList.get(i));
            tempLMO = new ListModelObject(regionList.get(i));
            tempDLM.addElement(tempLMO);
        }
        regionJL = new JList<>(tempDLM);
        regionJL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        regionJL.addListSelectionListener(this);

        POKE_SEARCH.setRegionList(regionList);
        POKE_SEARCH.setRegionInput(regionInput);

        PS_PokemonObject.setRegionList(regionList);
    }

    // TODO: Implement size, weight, abilities, and weakness options
    // TODO: Implement 'clear' button to search bar
    // Sets up the panels, buttons, lists, and text fields.
    private void setUpPanels() {
        GridBagConstraints gbc = new GridBagConstraints();
        ArrayList<JComponent> componentList = new ArrayList<>();
        DefaultListModel<ListModelObject> tempDLM;
        ListModelObject tempSLM;

        // Add the search bar to the screen
        JPanel searchBarPanel = new JPanel(new GridLayout(2, 1));
        searchBarPanel.setBackground(Color.RED);
        searchBarPanel.add(new JLabel("POKEMON SEARCH!"));
        searchTF = new JTextField(SEARCH_BAR_WIDTH);
        searchBarPanel.add(searchTF);
        componentList.add(searchBarPanel);
        searchTF.getDocument().addDocumentListener(this);

        componentList.add(new JLabel(BORDER));

        componentList.add(new JLabel("TYPE(S)"));

        // TODO: Implement check boxes
        // TODO: Rename checkboxes
        checkBox1 = new JCheckBox("(WIP)"); // Find pokemon that have both selected types (max 2 selections)
        checkBox1.setBackground(Color.RED);
        componentList.add(checkBox1);

        checkBox2 = new JCheckBox("(WIP) Exclude the selected types");
        checkBox2.setBackground(Color.RED);
        checkBox2.addItemListener(this);
        componentList.add(checkBox2);

        // Add the type list to the screen
        JScrollPane typePane = new JScrollPane(typeJL);
        typePane.setMinimumSize(new Dimension(TYPE_PANE_WIDTH, TYPE_PANE_HEIGHT));
        componentList.add(typePane);

        componentList.add(new JLabel(BORDER));

        componentList.add(new JLabel("REGION(S)"));

        // Add the region list to the screen
        JScrollPane regionPane = new JScrollPane(regionJL);
        regionPane.setMinimumSize(new Dimension(REGION_PANE_WIDTH, REGION_PANE_HEIGHT));
        componentList.add(regionPane);

        componentList.add(new JLabel(BORDER));

        // Add the evolution list to the screen
        tempDLM = new DefaultListModel<>();
        for (int i = 0; i < EVO_LIST.length; i++) {
            tempSLM = new ListModelObject("The " + EVO_LIST[i] + " Evolution");
            tempDLM.addElement(tempSLM);
        }
        evolutionJL = new JList<>(tempDLM);
        evolutionJL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        evolutionJL.addListSelectionListener(this);
        componentList.add(evolutionJL);

        componentList.add(new JLabel(BORDER));

        componentList.add(new JLabel("[Do \'Ctrl-Click\'' to multi-select or deselect items.]"));
        componentList.add(new JLabel("[Do \'Shift-Click to select all between two selections.]"));

        for (int b = 0; b < componentList.size(); b++) {
            MENU_PANEL.add(componentList.get(b), Misc.setGBC(gbc, 0, b));
        }
    }

    // Attempt to search by number.
    private void searchBarUpdate() {
        String input = searchTF.getText().replaceAll(" ", "");

        try {
            POKE_SEARCH.setInputNum(Integer.parseInt(input));
            POKE_SEARCH.searchByNumber();

        } catch (NumberFormatException n) {
            POKE_SEARCH.setInputText(input);
            POKE_SEARCH.findPokemon();
        }
    }

    private void getSelections(ListSelectionEvent l) {
        // Get the selected items from 'typeJL'
        if (l.getSource() == typeJL) {
            typeInput = new ArrayList<String>();

            for (int t : typeJL.getSelectedIndices()) {
                typeInput.add(typeList.get(t));
            }
            POKE_SEARCH.setTypeInput(typeInput);

            // Get the selected items from 'regionJL'
        } else if (l.getSource() == regionJL) {
            regionInput = new ArrayList<String>();

            for (int r : regionJL.getSelectedIndices()) {
                regionInput.add(regionList.get(r * 2));
            }

            if (regionInput.size() == 0) {
                for (int i = 0; i < regionList.size(); i = i + 2) {
                    regionInput.add(regionList.get(i));
                }
            }
            POKE_SEARCH.setRegionInput(regionInput);

            // Get the selected item from 'evolutionJL'
            // and translate it into 'evolutionInput'
        } else if (l.getSource() == evolutionJL) {
            evolutionInput = new ArrayList<String>();

            for (int e : evolutionJL.getSelectedIndices()) {
                evolutionInput.add("" + e);
            }
            POKE_SEARCH.setEvolutionInput(evolutionInput);
        }
        searchBarUpdate();
    }

    @Override
    public void valueChanged(ListSelectionEvent l) {
        if (l.getValueIsAdjusting()) {
            getSelections(l);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem() == checkBox2) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                POKE_SEARCH.setExcludeTypes(true);
            } else {
                POKE_SEARCH.setExcludeTypes(false);
            }
        }
        POKE_SEARCH.findPokemon();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        searchBarUpdate();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        searchBarUpdate();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        // Not Used
    }
}
