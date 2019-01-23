import Graph.Graph;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.IntBuffer;
import java.util.*;

public class Main extends Application {
    private FileInputStream inputStream;
    private byte [] data;
//    private Graph graph = new Graph();
    private static double huffmanTotalBit = 0;

    public static class HuffmanTree implements Comparable<HuffmanTree> {
        public final int frequency;
        public HuffmanTree(int frequency) {
            this.frequency = frequency;
        }

        @Override
        public int compareTo(HuffmanTree tree) {
            return frequency - tree.frequency;
        }

        public static HuffmanTree buildTree(Hashtable huffmanFreqTable) {
            PriorityQueue<HuffmanTree> tree = new PriorityQueue<>();
//            for (int i = 0; i < huffmanFreqTable.size(); i++) {
//                tree.offer(new HuffmanLeaf(, i));
//            }

            Enumeration keys = huffmanFreqTable.keys();
            Enumeration values = huffmanFreqTable.elements();
            int hashKey;
            int hashValue;
            while (keys.hasMoreElements()) {
                hashKey = (int)keys.nextElement();
                hashValue = (int)values.nextElement();
                tree.offer(new HuffmanLeaf(hashValue, hashKey));
            }

            // Loop until tree has one node
            while (tree.size() > 1) {
                HuffmanTree subTree1 = tree.poll();
                HuffmanTree subTree2 = tree.poll();
                tree.offer(new HuffmanNode(subTree1, subTree2));
            }
            return tree.poll();
        }
    }

    private static class HuffmanLeaf extends HuffmanTree {
        public final int leaf;

        public HuffmanLeaf(int freq, int val) {
            super(freq);
            leaf = val;
        }
    }

    private static class HuffmanNode extends HuffmanTree {
        public final HuffmanTree left, right;

        public HuffmanNode(HuffmanTree l, HuffmanTree r) {
            super(l.frequency + r.frequency);
            left = l;
            right = r;
        }
    }

    public static void huffCountTotalBit(HuffmanTree tree, int codeLength) {
        if (tree == null) { }
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf) tree;
            huffmanTotalBit = huffmanTotalBit + leaf.frequency * codeLength;
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode) tree;
            codeLength++;
            huffCountTotalBit(node.left, codeLength);
            codeLength--;

            codeLength++;
            huffCountTotalBit(node.right, codeLength);
//            codeLength--;
        }
    }

    public static class LZW {
        public static ArrayList encodeLZW(String uncompressed) {
            Map<String, Integer> dictionary = new HashMap<>();
            ArrayList<Integer> output = new ArrayList<>();
            int dictSize = 65536;
            int i = 0;
            for (char c : uncompressed.toCharArray()) {
                dictionary.put("" + c, i);
                i++;
            }

            String s = "";
            for (char c : uncompressed.toCharArray()) {
                String sc = s + c;
                if (dictionary.containsKey(sc)) {
                    s = sc;
                } else {
                    output.add(dictionary.get(s));
                    dictionary.put(sc, dictSize++);
                    s = "" + c;
                }
            }
            if (dictionary.containsKey(s)) {
                output.add(dictionary.get(s));
            }
            return output;
        }
    }

    public static void main(String args[]){
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a file");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("WAV", "*.wav")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            System.exit(1);
        }
        wavCompression(file, primaryStage);

        primaryStage.show();
    }

    private void wavCompression(File file, Stage primaryStage) throws IOException {
        inputStream = new FileInputStream(file);
        data = new byte[(int)file.length()];
        inputStream.read(data);
        int bytePerSample = ((data[34] & 0xff) | (data[35] & 0xff)<<8) / 8;
//        int sampleTotal = ((((((data[40] & 0xff) | (data[41] & 0xff) << 8) | (data[42] & 0xff) << 16) | (data[43] & 0xff) << 24))/ bytePerSample);
        int sampleTotal = ((data.length - 44) / bytePerSample);
        double sampleInBit = sampleTotal * bytePerSample * 8;
        int[] uncompressData = new int[sampleTotal];
        for (int i = 0; i < sampleTotal; i++) {
            uncompressData[i] = (data[44 + i * 2] & 0xff) | (data[45 + i * 2] & 0xff)<<8;
//            if (uncompressData[i] > 32768) {
//                uncompressData[i] = uncompressData[i] - 65536;
//            }
        }


        // Huffman coding
        Hashtable<Integer, Integer> huffmanFreqTable = new Hashtable<>();
        int codeLength = 0;
        for (int i = 0; i < uncompressData.length; i++) {
            if(huffmanFreqTable.containsKey(uncompressData[i])) {
                huffmanFreqTable.replace(uncompressData[i], huffmanFreqTable.get(uncompressData[i])+1);
            } else {
                huffmanFreqTable.put(uncompressData[i], 1);
            }
        }

//        Enumeration keys = huffmanFreqTable.keys();
//        Enumeration values = huffmanFreqTable.elements();
//        int hashKey[] = new int[huffmanFreqTable.size()];
//        int hashValue[] = new int[huffmanFreqTable.size()];
//        int count = 0;
//        while (keys.hasMoreElements()) {
//            hashKey[count] = (int)keys.nextElement();
//            hashValue[count] = (int)values.nextElement();
//            count++;
//        }
//        double entropy = 0;
//        double totalFreq = 0;
//        for (int i = 0; i < huffmanFreqTable.size(); i++) {
//            totalFreq += hashValue[i];
//        }
//        for (int i = 0; i < huffmanFreqTable.size(); i++) {
//            entropy += -1 * (hashValue[i]/totalFreq) * (Math.log(hashValue[i]/totalFreq) / Math.log(2));
//        }
//        System.out.println(entropy);

        HuffmanTree huffmanTree = HuffmanTree.buildTree(huffmanFreqTable);
        huffCountTotalBit(huffmanTree, codeLength);
        double huffmanRate = sampleInBit / huffmanTotalBit;

        primaryStage.setTitle(".wav file");
        VBox root = new VBox(10);
        root.setPadding(new Insets(8));
        Text huffCompressionRate = new Text();
        huffCompressionRate.setText("Huffman coding compression rate: " + huffmanRate);


        // LZW coding
        String uncompressed = Arrays.toString(uncompressData);
        ArrayList codeLengthLZW = LZW.encodeLZW(uncompressed);
        double rateLZW = sampleTotal * bytePerSample / (codeLengthLZW.size() * 3.0);
        Text lzwCompressionRate = new Text();
        lzwCompressionRate.setText("LZW compression rate: " + rateLZW);
        root.getChildren().addAll(huffCompressionRate, lzwCompressionRate);


        Scene scene  = new Scene(root);
        primaryStage.setScene(scene);
    }
}
