import React, { Component } from "react";

import axios from "axios";

import {
  ForceGraph2D,
  ForceGraph3D,
  ForceGraphVR,
  ForceGraphAR,
} from "react-force-graph";

class GraphContainer extends Component {
  state = {
    dataParsed: false,
    nodeIds: [],
    nodes: [],
    links: [],
  };

  shouldComponentUpdate(nextProps, nextState) {
    if (nextState.dataParsed !== this.state.dataParsed) return true;
  }

  generateGraph(data) {}

  componentDidMount() {
    console.log("GraphContainer", "componentDidMount");

    if (this.props.fileLoaded) {
      axios
        .get("kgsum_result.json")
        .then((res) => {
          res.data["@graph"].forEach((element, index) => {
            if (index > 800) {
              this.setState({
                dataParsed: true,
              });
            } else {
              if (typeof element["@id"] === "string") {
                this.handleNode(element, element["@id"], index);
              } else if (typeof element["@id"] === "object") {
                element["@id"].forEach((elementType) => {
                  this.handleNode(element, elementType, index);
                });
              } else {
                // undefined
                // console.log(typeof element["@type"], element);
              }
            }
          });
        })
        .catch((err) => console.log("JSON ERROR", err));
      // getData();
    }
  }

  arrayAdd(array, element) {
    const newArray = array.slice();
    newArray.push(element);
    return newArray;
  }

  handleObject(elemId, object, objectType, group) {
    this.setState((prevState) => {
      if (!prevState.nodeIds.includes(object)) {
        const uriParts = object.split("/");
        return {
          nodeIds: this.arrayAdd(prevState.nodeIds, object),
          nodes: this.arrayAdd(prevState.nodes, {
            id: object,
            name: decodeURIComponent(uriParts[uriParts.length - 1]),
            group: group,
          }),
          links: this.arrayAdd(prevState.links, {
            source: elemId,
            target: object,
            name: objectType,
            // value: group,
          }),
        };
      } else {
        return {
          links: this.arrayAdd(prevState.links, {
            source: elemId,
            target: object,
            name: objectType,
            // value: group,
          }),
        };
      }
    });
  }

  handleNode(element, elemType, group) {
    this.setState((prevState) => {
      if (!prevState.nodeIds.includes(element["@id"])) {
        let uriParts = element["@id"].split("/");

        let typeParts = elemType.split("/");
        const type = typeParts[typeParts.length - 1];

        const regex = /^[^@]/g;

        Object.keys(element).forEach((key) => {
          if (key.match(regex)) {
            if (typeof element[key] === "string") {
              this.handleObject(element["@id"], element[key], key, group);
            } else if (typeof element[key] === "object") {
              element[key].forEach((subProp) => {
                this.handleObject(element["@id"], subProp, key, group);
              });
            }
          }
        });

        // this.state.typeProps[type].forEach((prop) => {
        //   if (prop in element) {
        //     if (typeof element[prop] === "string") {
        //       this.handleObject(element["@id"], element[prop], prop, group);
        //     } else if (typeof element[prop] === "object") {
        //       element[prop].forEach((subProp) => {
        //         this.handleObject(element["@id"], subProp, prop, group);
        //       });
        //     }
        //   }
        // });
        return {
          nodeIds: this.arrayAdd(prevState.nodeIds, element["@id"]),
          nodes: this.arrayAdd(prevState.nodes, {
            id: element["@id"],
            name: decodeURIComponent(uriParts[uriParts.length - 1]),
            group: group,
          }),
        };
      }
    });
  }

  render() {
    let graph = null;

    if (this.state.dataParsed) {
      let graphData = {
        nodes: this.state.nodes,
        links: this.state.links,
      };

      graph = (
        <div className="graph-vis">
          <ForceGraph3D
            graphData={graphData}
            nodeRelSize={6}
            linkDirectionalArrowLength={3.5}
            linkDirectionalArrowRelPos={1}
            linkCurvature={0.0}
            nodeLabel="name"
            width={1000}
            enableNavigationControls="true"
            controlType="trackball"
            nodeAutoColorBy="group"
            linkWidth={1}
            linkDirectionalParticles={1}
          />
        </div>
      );
    }

    return (
      <div class="graph-vis">
        <div class="info-block">
          <div class="return">
            <button>
              <a href="/" goback>
                Back
              </a>
            </button>
          </div>
          <div className="downloads">
            <button>
              <a href="/kgsum_result.ttl" download>
                Download Summmarized File (TTL)
              </a>
            </button>

            <button>
              <a href="/kgsum_result.json" download>
                Download Summmarized File (JSON-LD)
              </a>
            </button>
          </div>
        </div>

        <div class="graph-container">{graph}</div>
      </div>
    );
  }
}

export default GraphContainer;
