/*
 * Project Name: kmfex-platform
 * File Name: ObjectConverter.java
 * Class Name: ObjectConverter
 *
 * Copyright 2014 KMFEX Inc
 *
 * 
 *
 * http://www.kmfex.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fushun.framework.util.beans;

/**
 * Class Name: ObjectConverter
 *
 * @author wenzc
 */
public interface ObjectConverter<F, T> {

    //convert it to cmp
    void convertFromDto(T domain, F target);

    //convert it to dto
    void convertToDto(F source, T domain);

}
