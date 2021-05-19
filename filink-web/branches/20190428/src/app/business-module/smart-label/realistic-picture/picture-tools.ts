export class PictureTools {
  private static graph;

  public static init(graph) {
    PictureTools.graph = graph;
  }
  public static createEquipment(Q, graph, name, x, y, image, host?) {
    const node = graph.createNode(name, x, y);
    node.anchorPosition = Q.Position.LEFT_TOP;
    node.image = image;
    if (host) {
      node.host = host;
      node.parent = host;
    }
    return node;
  }

}
