package pl.touk.mockserver.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Data
@EqualsAndHashCode(callSuper = false)
public class PeekMock extends MockServerRequest{
    @XmlElement(required = true)
    private String name;
}