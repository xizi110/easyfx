package xyz.yuelai.util;

import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SVGUtil {

    private static final String parser = XMLResourceDescriptor.getXMLParserClassName();
    private static final SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

    public static List<SVGPath> getSVGPaths(File svgFile) throws IOException {
        Document document = factory.createDocument(svgFile.toURI().toString());
        return readPath(document);
    }

    private static List<SVGPath> readPath(Document document) {
        NodeList paths = document.getElementsByTagName("path");
        ArrayList<SVGPath> pathList = new ArrayList<>();
        for (int i = 0; i < paths.getLength(); i++) {
            Node item = paths.item(i);
            SVGPath svgPath = getSVGPath(item);
            pathList.add(svgPath);
        }
        return pathList;
    }

    private static SVGPath getSVGPath(Node path) {
        NamedNodeMap attributes = path.getAttributes();
        SVGPath svgPath = new SVGPath();
        boolean fill = false;
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            String nodeName = attribute.getNodeName();
            String nodeValue = attribute.getNodeValue();
            switch (nodeName) {
                case "d": {
                    svgPath.setContent(nodeValue);
                    break;
                }
                case "fill": {
                    if (Objects.equals("none", nodeValue)) {
                        svgPath.setFill(Color.TRANSPARENT);
                    } else {
                        svgPath.setFill(Color.valueOf(nodeValue));
                    }
                    fill = true;
                    break;
                }
                case "stroke": {
                    svgPath.setStroke(Color.valueOf(nodeValue));
                    break;
                }
                case "stroke-linejoin": {
                    svgPath.setStrokeLineJoin(StrokeLineJoin.valueOf(nodeValue.toUpperCase()));
                    break;
                }
                case "stroke-width": {
                    svgPath.setStrokeWidth(Double.parseDouble(nodeValue));
                    break;
                }
                case "stroke-dashoffset": {
                    svgPath.setStrokeDashOffset(Double.parseDouble(nodeValue));
                    break;
                }
                case "stroke-miterlimit": {
                    svgPath.setStrokeMiterLimit(Double.parseDouble(nodeValue));
                    break;
                }
                case "stroke-linecap": {
                    svgPath.setStrokeLineCap(StrokeLineCap.valueOf(nodeValue.toUpperCase()));
                    break;
                }
                case "fill-rule": {
                    String fillRule = nodeValue.toUpperCase();
                    if ("nonzero".equals(fillRule)){
                        svgPath.setFillRule(FillRule.NON_ZERO);
                    }
                    if ("evenodd".equals(fillRule)){
                        svgPath.setFillRule(FillRule.EVEN_ODD);
                    }
                    break;
                }
            }
        }
        if (!fill) {
            svgPath.setFill(Color.TRANSPARENT);
        }
        return svgPath;
    }

}
